/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_FeatureTest extends IntactBasicTestCase {

    @Before
    public void before() throws Exception {
        beginTransaction();
    }

    @After
    public void after() throws Exception {
        commitTransaction();
    }

    @Test
    public void persistFeature() throws Exception {
        Feature feature = getMockBuilder().createFeatureRandom();
        PersisterHelper.saveOrUpdate( feature );

        Assert.assertNotNull( feature.getCvFeatureType() );
    }

    @Test
    public void persistFeatureWithRange() throws Exception {

        final String seq = "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" + // 60
                           "QEDYDRLRPLSYPQTDVFLVCFSVVSPSSFENVKEKWVPEITHHCPKTPFLLVGTQIDLR" + // 120
                           "DDPSTIEKLAKNKQKPITPETAEKLARDLKAVKYVECSALTQRGLKNVFDEAILAALEPP" + // 180
                           "ETQPKRKCCIF";                                                   // 191

        IntactMockBuilder mockBuilder = new IntactMockBuilder( IntactContext.getCurrentInstance().getInstitution() );
        Experiment exp = mockBuilder.createExperimentEmpty( "kerrien-2007-1" );
        Protein bait = mockBuilder.createProtein( "P12345", "foo" );
        bait.setSequence( seq );
        Protein prey = mockBuilder.createProtein( "Q98765", "bar" );
        Interaction interaction = mockBuilder.createInteraction( "foo-bar", bait, prey, exp );

        Assert.assertEquals( 2, interaction.getComponents().size() );
        Assert.assertEquals( 1, bait.getActiveInstances().size() );
        Assert.assertEquals( 1, prey.getActiveInstances().size() );

        for ( Component component : interaction.getComponents() ) {
            if ( component.getInteractor() == bait ) {
                // add a feature
                Feature feature = mockBuilder.createFeatureRandom();
                feature.setCvFeatureType( mockBuilder.createCvObject( CvFeatureType.class, "MI:0117", "binding site" ) );
                Range range = mockBuilder.createRange( 4, 4, 20, 20 ); // KCVVVGDGAVGKTCLL
                range.setFromCvFuzzyType( null );
                range.setToCvFuzzyType( null );
                feature.addRange( range );
                range.prepareSequence( bait.getSequence() );
                Assert.assertEquals( 100, range.getSequence().length() );
                Assert.assertEquals( seq.substring( 4, 104 ), range.getSequence() );
                component.addBindingDomain( feature );
            }
        }

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals( 1, bait.getActiveInstances().size() );
    }

    @Test
    public void persistFeature_sameLabelDifferentComponents() throws Exception {
        Feature feature1 = getMockBuilder().createFeatureRandom();
        Feature feature2 = getMockBuilder().createFeatureRandom();
        feature2.setShortLabel( feature1.getShortLabel() );

        PersisterHelper.saveOrUpdate(feature1, feature2);

        Assert.assertEquals( 2, getDaoFactory().getFeatureDao().countAll() );
    }

    @Test
    public void persistFeatureWithRangeWithoutFuzzyType() throws Exception {
        Feature feature = getMockBuilder().createFeatureRandom();

        final Range range = getMockBuilder().createRange( 1, 1, 2, 2 );
        range.setFromCvFuzzyType( null );
        range.setToCvFuzzyType( null );
        feature.addRange( range );

        PersisterHelper.saveOrUpdate( feature );
    }
}