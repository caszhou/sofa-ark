/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.ark.loader.exploded;

import com.alipay.sofa.ark.loader.archive.ExplodedArchive;
import com.alipay.sofa.ark.spi.archive.Archive;
import com.alipay.sofa.ark.spi.archive.BizArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExplodedDirectoryArchive extends ExplodedArchive {
    public ExplodedDirectoryArchive(File root) {
        super(root);
    }

    public ExplodedDirectoryArchive(File root, boolean recursive) {
        super(root, recursive);
    }

    public Archive getNestedArchive(Entry entry) throws IOException {
        File file = ((FileEntry) entry).getFile();
        return (file.isDirectory() ? new ExplodedDirectoryArchive(file, true) : new FileArchive(
            file));
    }

    public URL[] getUrls(EntryFilter entryFilter) throws IOException {
        List<Archive> archives = getNestedArchives(entryFilter);

        List<URL> urls = new ArrayList<>(archives.size() + 1);
        urls.add(getUrl());

        for (Archive archive : archives) {
            urls.add(archive.getUrl());
        }

        return urls.toArray(new URL[urls.size()]);
    }

    public static void main(String[] args) throws Exception {
        ExplodedDirectoryArchive directoryArchive = new ExplodedDirectoryArchive(new File(
            "/Users/syd/java/fundapplication/plugin/"));
        ExplodedExecutableArkBizJar bizJar = new ExplodedExecutableArkBizJar(directoryArchive);
        for (BizArchive bizArchive : bizJar.getBizArchives()) {
            System.out.println("biz:" + bizArchive.getUrl());
            //            printAchive(((JarBizArchive) bizArchive).getExportUrls());

            printAchive(bizArchive.getUrls());
        }
        //        ContainerArchive containerArchive = bizJar.getContainerArchive();
        //        System.out.println("container:" + containerArchive.getUrls().length + ","
        //                + containerArchive.getUrl());
        //        printAchive(containerArchive.getUrls());
        //
        //        for (PluginArchive pluginArchive : bizJar.getPluginArchives()) {
        //            System.out.println("plugin:" + pluginArchive.getUrl());
        //            printAchive(pluginArchive.getUrls());
        //        }
    }

    public static void printAchive(URL[] urls) {
        int i = 0;
        for (URL url : urls) {
            if (url.getPath().contains("log4j-1.2.16"))
                System.out.println((i++) + "," + url);
        }
    }
}
