/*
 * ODPLocalInputComponentTest.java
 * 
 * Created on 2004-06-29
 */
package com.stachoodev.carrot.input.odp.local;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.tools.*;

import junit.framework.*;

/**
 * @author stachoo
 */
public class ODPLocalInputComponentTest extends TestCase
{
    /** Temporary directory for index data */
    private File temporaryDirectory;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        // Create a temporary index
        temporaryDirectory = TempUtils.createTemporaryDirectory("ctmtest");

        String odpInput = "<?xml version='1.0' encoding='UTF-8' ?>"
            + "    <RDF xmlns:r='http://www.w3.org/TR/RDF/'"
            + "         xmlns:d='http://purl.org/dc/elements/1.0/'"
            + "         xmlns='http://dmoz.org/rdf'>"
            + "      <Topic r:id='Top/World/Polska/Komputery'>"
            + "        <catid>32460</catid>"
            + "      </Topic>"
            + "      <ExternalPage about='http://www.agh.edu.pl/ogonki/'>"
            + "        <d:Title>Polska Strona Ogonkowa</d:Title>"
            + "        <d:Description>Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery</topic>"
            + "      </ExternalPage>"
            + "      <Topic r:id='Top/World/Polska/Komputery2'>"
            + "        <catid>32461</catid>"
            + "      </Topic>"
            + "      <Topic r:id='Top/World/Polska/Komputery2'>"
            + "        <catid>32462</catid>"
            + "      </Topic>"
            + "      <ExternalPage about='http://www.gust.org.pl/'>"
            + "        <d:Title>GUST</d:Title>"
            + "        <d:Description>Grupa użytkowników systemu Tex.</d:Description>"
            + "        <topic>Top/World/Polska/Komputery2</topic>"
            + "      </ExternalPage>" + "    </RDF>";

        InputStream odpInputStream = new ByteArrayInputStream(odpInput
            .getBytes("UTF8"));

        ODPIndexer indxer = new ODPIndexer(temporaryDirectory.getPath());
        indxer.index(odpInputStream);

        ODPIndex.initialize(temporaryDirectory.getPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        ODPIndex.dispose();
        TempUtils.deleteDirectory(temporaryDirectory);
        super.tearDown();
    }

    /**
     * @throws Exception
     * @throws MissingProcessException
     *  
     */
    public void testCatidMixer() throws MissingProcessException, Exception
    {
        // ODP input component factory
        LocalComponentFactory inputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new ODPLocalInputComponent();
            }
        };

        // Some output component
        LocalComponentFactory outputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new DocumentsConsumerOutputComponent();
            }
        };

        // Register with the controller
        LocalController controller = new LocalControllerBase();
        controller.addLocalComponentFactory("input.odp", inputFactory);
        controller.addLocalComponentFactory("output", outputFactory);

        // Create and register the process
        LocalProcessBase process = new LocalProcessBase();
        process.setInput("input.odp");
        process.setOutput("output");
        controller.addProcess("process.odp-test", process);

        List results = (List) controller.query("process.odp-test",
            "catid: 32460", new HashMap()).getQueryResult();

        RawDocument document01 = new RawDocumentSnippet(
            "Polska Strona Ogonkowa",
            "Co zrobić aby zobaczyć w tekstach polskie litery: informacje, fonty, programy.");
        document01.setProperty(ODPLocalInputComponent.PROPERTY_ODP_CATID,
            "32460");
        List expectedResults = Arrays.asList(new RawDocument []
        { document01 });

        assertEquals("RawDocuments list", expectedResults, results);
    }
}