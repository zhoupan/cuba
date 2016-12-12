/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.cuba.core.sys;

import com.haulmont.bali.util.Dom4j;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.global.Stores;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;

@Component("cuba_MetadataBuildSupport")
public class MetadataBuildSupport {

    public static class EntityClassInfo {
        public final String store;
        public final String name;

        public EntityClassInfo(String store, String name) {
            this.store = store;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " - " + store;
        }
    }

    public static class XmlAnnotations {
        public final String name;
        public final Map<String, String> annotations = new HashMap<>();
        public final List<XmlAnnotations> attributeAnnotations = new ArrayList<>();

        public XmlAnnotations(String name) {
            this.name = name;
        }
    }

    public static class XmlFile {
        public final String name;
        public final Element root;

        public XmlFile(String name, Element root) {
            this.name = name;
            this.root = root;
        }
    }

    private Logger log = LoggerFactory.getLogger(MetadataBuildSupport.class);

    public static final String PERSISTENCE_CONFIG = "cuba.persistenceConfig";
    public static final String METADATA_CONFIG = "cuba.metadataConfig";

    @Inject
    protected Resources resources;

    public List<XmlFile> init() {
        List<XmlFile> metadataXmlList = new ArrayList<>();
        StrTokenizer metadataFilesTokenizer = new StrTokenizer(getMetadataConfig());
        for (String fileName : metadataFilesTokenizer.getTokenArray()) {
            metadataXmlList.add(new XmlFile(fileName, readXml(fileName)));
        }
        return metadataXmlList;
    }

    /**
     * @param storeName data store name
     * @return location of persistent entities descriptor or null if not defined
     */
    @Nullable
    public String getPersistenceConfig(String storeName) {
        String propName = PERSISTENCE_CONFIG;
        if (!Stores.isMain(storeName))
            propName = propName + "_" + storeName;

        String config = AppContext.getProperty(propName);
        if (StringUtils.isBlank(config)) {
            log.trace("Property {} is not set, assuming {} is not a RdbmsStore", propName, storeName);
            return null;
        } else
            return config;
    }

    /**
     * @return location of metadata descriptor
     */
    public String getMetadataConfig() {
        String config = AppContext.getProperty(METADATA_CONFIG);
        if (StringUtils.isBlank(config))
            throw new IllegalStateException(METADATA_CONFIG + " application property is not defined");
        return config;
    }

    public List<Element> getDatatypeElements(List<XmlFile> metadataXmlList) {
        List<Element> list = new ArrayList<>();
        for (XmlFile xmlFile : metadataXmlList) {
            Element datatypesEl = xmlFile.root.element("datatypes");
            if (datatypesEl != null) {
                list.addAll(Dom4j.elements(datatypesEl, "datatype"));
            }
        }
        return list;
    }

    public Map<String, List<EntityClassInfo>> getEntityPackages(List<XmlFile> metadataXmlList) {
        Map<String, List<EntityClassInfo>> packages = new LinkedHashMap<>();

        loadFromMetadataConfig(packages, metadataXmlList);
        Stores.getAll().forEach(db -> loadFromPersistenceConfig(packages, db));

        return packages;
    }

    protected void loadFromMetadataConfig(Map<String, List<EntityClassInfo>> packages, List<XmlFile> metadataXmlList) {
        for (XmlFile xmlFile : metadataXmlList) {
            for (Element element : Dom4j.elements(xmlFile.root, "metadata-model")) {
                String rootPackage = element.attributeValue("root-package");
                if (StringUtils.isBlank(rootPackage))
                    throw new IllegalStateException("metadata-model/@root-package is empty in " + xmlFile.name);

                List<EntityClassInfo> classNames = packages.get(rootPackage);
                if (classNames == null) {
                    classNames = new ArrayList<>();
                    packages.put(rootPackage, classNames);
                }
                for (Element classEl : Dom4j.elements(element, "class")) {
                    classNames.add(new EntityClassInfo(classEl.attributeValue("store"), classEl.getText().trim()));
                }
            }
        }
    }

    protected void loadFromPersistenceConfig(Map<String, List<EntityClassInfo>> packages, String db) {
        String persistenceConfig = getPersistenceConfig(db);
        if (persistenceConfig == null)
            return;
        StrTokenizer persistenceFilestokenizer = new StrTokenizer(persistenceConfig);
        for (String fileName : persistenceFilestokenizer.getTokenArray()) {
            Element root = readXml(fileName);
            Element puEl = root.element("persistence-unit");
            if (puEl == null)
                throw new IllegalStateException("File " + fileName + " has no persistence-unit element");

            for (Element classEl : Dom4j.elements(puEl, "class")) {
                String className = classEl.getText().trim();
                boolean included = false;
                for (String rootPackage : packages.keySet()) {
                    if (className.startsWith(rootPackage + ".")) {
                        List<EntityClassInfo> classNames = packages.get(rootPackage);
                        if (classNames == null) {
                            classNames = new ArrayList<>();
                            packages.put(rootPackage, classNames);
                        }
                        classNames.add(new EntityClassInfo(db, className));
                        included = true;
                        break;
                    }
                }
                if (!included)
                    throw new IllegalStateException("Can not find a model for class " + className
                            + ". The class's package must be inside of some model's root package.");
            }
        }
    }

    protected Element readXml(String path) {
        InputStream stream = resources.getResourceAsStream(path);
        try {
            stream = resources.getResourceAsStream(path);
            if (stream == null)
                throw new IllegalStateException("Resource not found: " + path);
            Document document = Dom4j.readDocument(stream);
            return document.getRootElement();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public List<XmlAnnotations> getEntityAnnotations(List<XmlFile> metadataXmlList) {
        List<XmlAnnotations> result = new ArrayList<>();

        for (XmlFile xmlFile : metadataXmlList) {
            Element annotationsEl = xmlFile.root.element("annotations");
            if (annotationsEl != null) {
                for (Element entityEl : Dom4j.elements(annotationsEl, "entity")) {
                    String className = entityEl.attributeValue("class");
                    XmlAnnotations entityAnnotations = new XmlAnnotations(className);
                    for (Element annotEl : Dom4j.elements(entityEl, "annotation")) {
                        entityAnnotations.annotations.put(annotEl.attributeValue("name"), annotEl.attributeValue("value"));
                    }
                    for (Element propEl : Dom4j.elements(entityEl, "property")) {
                        XmlAnnotations attributeAnnotations = new XmlAnnotations(propEl.attributeValue("name"));
                        for (Element annotEl : Dom4j.elements(propEl, "annotation")) {
                            attributeAnnotations.annotations.put(annotEl.attributeValue("name"), annotEl.attributeValue("value"));
                        }
                        entityAnnotations.attributeAnnotations.add(attributeAnnotations);
                    }
                    result.add(entityAnnotations);
                }
            }
        }
        return result;
    }
}