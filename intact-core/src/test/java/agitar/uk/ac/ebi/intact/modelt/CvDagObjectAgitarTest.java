/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:25:14
 * Time to generate: 01:53.235 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt;

import com.agitar.lib.junit.AgitarTestCase;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;

public class CvDagObjectAgitarTest extends AgitarTestCase {

    static Class TARGET_CLASS = CvDagObject.class;

    public void testAddChild1() throws Throwable {
        Institution owner = new Institution( "testCvLabel" );
        CvDagObject cvInteraction = new CvInteraction( new Institution( "testCvLabel1" ), "testCvLabel" );
        cvInteraction.addChild( new CvFeatureIdentification( owner, "testCvLabel" ) );
        cvInteraction.addChild( new CvFeatureType( owner, "testCvDagObject\rShortLabel" ) );
        assertEquals( "(CvInteraction) cvInteraction.getChildren().size()", 2, cvInteraction.getChildren().size() );
    }

    public void testAncestors() throws Throwable {
        CvDagObject cvFeatureType = new CvFeatureType( new Institution( "testCvLabel" ), "testCvLabel" );
        ArrayList result = ( ArrayList ) cvFeatureType.ancestors();
        assertEquals( "result.size()", 1, result.size() );
        assertTrue( "(ArrayList) result.contains(cvFeatureType)", result.contains( cvFeatureType ) );
    }

    public void testGetRoot() throws Throwable {
        CvDagObject cvInteraction = new CvInteraction( null, "testCvLabel" );
        CvDagObject result = cvInteraction.getRoot();
        assertSame( "result", cvInteraction, result );
        assertEquals( "(CvInteraction) cvInteraction.getParents().size()", 0, cvInteraction.getParents().size() );
    }

    public void testGetRoot1() throws Throwable {
        Collection parents = new ArrayList( 100 );
        CvFeatureType cvFeatureType = new CvFeatureType( new Institution( "testCvLabel" ), "testCvLabel" );
        parents.add( cvFeatureType );
        CvDagObject cvFeatureIdentification = new CvFeatureIdentification( new Institution( "testCvLabel1" ), "testCvLabel" );
        super.callPrivateMethod("uk.ac.ebi.intact.model.CvDagObject", "setParents", new Class[]{Collection.class}, cvFeatureIdentification, new Object[]{parents} );

//        cvFeatureIdentification.setParents( parents );
        CvFeatureType result = ( CvFeatureType ) cvFeatureIdentification.getRoot();
        assertSame( "result", cvFeatureType, result );
        assertSame( "(CvFeatureIdentification) cvFeatureIdentification.getParents()", parents, cvFeatureIdentification.getParents() );
    }

    public void testHasChildren() throws Throwable {
        CvDagObject cvFeatureIdentification = new CvFeatureIdentification( new Institution( "testCvLabel" ), "testCvLabel" );
        Collection children = new ArrayList( 100 );
        boolean add = children.add( new CvInteraction( new Institution( "testCvLabel1" ), "testCvLabel" ) );

        super.callPrivateMethod("uk.ac.ebi.intact.model.CvDagObject", "setChildren", new Class[]{Collection.class}, cvFeatureIdentification, new Object[]{children} );

//        cvFeatureIdentification.setChildren( children );
        boolean result = cvFeatureIdentification.hasChildren();
        assertTrue( "result", result );
    }

    public void testHasChildren1() throws Throwable {
        boolean result = new CvFeatureIdentification( new Institution( "testCvLabel" ), "testCvLabel" ).hasChildren();
        assertFalse( "result", result );
    }

    public void testRemoveParent() throws Throwable {
        CvDagObject cvDagObject = new CvFeatureIdentification( new Institution( "testCvLabel" ), "testCvLabel" );
        CvDagObject cvDagObject2 = new CvFeatureIdentification( new Institution( "testCvLabel1" ), "testCvLabel1" );
        cvDagObject.addChild( cvDagObject2 );
        cvDagObject2.removeParent( cvDagObject );
        assertEquals( "(CvFeatureIdentification) cvDagObject2.getParents().size()", 0, cvDagObject2.getParents().size() );
    }

    public void testSetChildren() throws Throwable {
        Collection children = new ArrayList( 100 );
        CvDagObject cvFeatureIdentification = new CvFeatureIdentification( new Institution( "testCvLabel" ), "testCvLabel" );
        super.callPrivateMethod("uk.ac.ebi.intact.model.CvDagObject", "setChildren", new Class[]{Collection.class}, cvFeatureIdentification, new Object[]{children} );

//        cvFeatureIdentification.setChildren( children );
        assertSame( "(CvFeatureIdentification) cvFeatureIdentification.getChildren()", children, cvFeatureIdentification.getChildren() );
    }

    public void testSetParents() throws Throwable {
        Collection parents = new ArrayList( 100 );
        CvDagObject cvFeatureIdentification = new CvFeatureIdentification( new Institution( "testCvLabel" ), "testCvLabel" );
//        cvFeatureIdentification.setParents( parents );
        super.callPrivateMethod("uk.ac.ebi.intact.model.CvDagObject", "setParents", new Class[]{Collection.class}, cvFeatureIdentification, new Object[]{parents} );

        assertSame( "(CvFeatureIdentification) cvFeatureIdentification.getParents()", parents, cvFeatureIdentification.getParents() );
    }

    public void testSetParentsThrowsIllegalArgumentException() throws Throwable {
        CvDagObject cvInteractionType = new CvInteractionType( new Institution( "testCvLabel" ), "testCvLabel" );
        try {
            super.callPrivateMethod("uk.ac.ebi.intact.model.CvDagObject", "setParents", new Class[]{Collection.class}, cvInteractionType, new Object[]{null} );
//            cvInteractionType.setParents( null );
            fail( "Expected IllegalArgumentException to be thrown" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "ex.getMessage()", "Parents cannot be null.", ex.getMessage() );
            assertThrownBy( CvDagObject.class, ex );
            assertEquals( "(CvInteractionType) cvInteractionType.getParents().size()", 0, cvInteractionType.getParents().size() );
        }
    }
}
