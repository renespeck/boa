package de.uni_leipzig.simba.boa.backend.test;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class ContextTest {

	// initialize logging and settings
	NLPediaSetup setup = null;
	NLPediaLogger logger = null;
	
	public static junit.framework.Test suite() {
		
		return new JUnit4TestAdapter(ContextTest.class);
	}
	
	@Before
	public void setUp() {
		
		this.setup = new NLPediaSetup(true);
		this.logger = new NLPediaLogger(ContextTest.class);
	}

	@After
	public void cleanUpStreams() {
	    
		this.setup.destroy();
	}
	
	@Test
	public void testContext() {
		
		String testAnnotated 			= "Has_O a_O of_O Josephine_B-PER of_O of_O Daughter_I-PER ,_O who_O was_O born_O in_O Germany_LOC ._O";
		String test			 			= "Has a of Josephine of of Daughter , who was born in Germany .";
		String patternWithOutVariables1 = ", who was born in";
		
		Context leftContext1 = new LeftContext(testAnnotated, test, patternWithOutVariables1);
		Context rightContext1 = new RightContext(testAnnotated, test, patternWithOutVariables1);
		
		assertTrue("Daughter".equals(leftContext1.getSuitableEntity("http://dbpedia.org/ontology/Person")));
		assertTrue("Germany".equals(rightContext1.getSuitableEntity("http://dbpedia.org/ontology/Country")));
		
		// ######################################################################
		
		patternWithOutVariables1	= "is on the outskirts of";
		testAnnotated				= "The_O Chernaya_B-LOC River_I-LOC is_O on_O the_O outskirts_O of_O Sevastopol_B-LOC.";
		test 						= "The Chernaya River is on the outskirts of Sevastopol.";
		
		Context leftContext2 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
		Context rightContext2 = new RightContext(testAnnotated,test, patternWithOutVariables1);
		
		assertTrue("Chernaya River".equals(leftContext2.getSuitableEntity("http://dbpedia.org/ontology/Place")));
		assertTrue("Sevastopol".equals(rightContext2.getSuitableEntity("http://dbpedia.org/ontology/Place")));
		
		// ######################################################################
		
		testAnnotated 				= "At_O stake_O were_O the_O 450_O seats_O in_O the_O State_B-ORG Duma_I-ORG -LRB-_O Gosudarstvennaya_B-PER Duma_I-PER -RRB-_O ,_O the_O lower_O house_O of_O the_O Federal_B-ORG Assembly_I-ORG of_O Russia_B-LOC -LRB-_O The_O legislature_O -RRB-_O ._O"; 
		test 						= "At stake were the 450 seats in the State Duma -LRB- Gosudarstvennaya Duma -RRB- , the lower house of the Federal Assembly of Russia -LRB- The legislature -RRB- .";
		patternWithOutVariables1 	= "-LRB- Gosudarstvennaya Duma -RRB- , the lower house of the";
		
		Context	leftContext3 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
		Context rightContext3 = new RightContext(testAnnotated,test, patternWithOutVariables1);
		
		assertTrue("State Duma".equals(leftContext3.getSuitableEntity("http://dbpedia.org/ontology/Legislature")));
		assertTrue("Federal Assembly".equals(rightContext3.getSuitableEntity("http://dbpedia.org/ontology/Legislature")));

		// ######################################################################

		testAnnotated 				= "In_O 2007_O Fiat_B-ORG Automobiles_I-ORG SpA_I-ORG relaunched_O the_O brand_O with_O the_O Grande_B-ORG Punto_I-ORG Abarth_I-ORG and_O the_O Grande_B-ORG Punto_I-ORG Abarth_I-ORG S2000_O ._O"; 
		test 						= "In 2007 Fiat Automobiles SpA relaunched the brand with the Grande Punto Abarth and the Grande Punto Abarth S2000 .";
		patternWithOutVariables1 	= "Grande Punto";
		
		Context leftContext4 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
		Context rightContext4 = new RightContext(testAnnotated,test, patternWithOutVariables1);
		
		assertTrue("Fiat Automobiles SpA".equals(leftContext4.getSuitableEntity("http://dbpedia.org/ontology/Legislature")));
		assertTrue("Abarth".equals(rightContext4.getSuitableEntity("http://dbpedia.org/ontology/Legislature")));
		
		// ######################################################################

		testAnnotated 				= "In_O 1989_O ,_O a_O new_O subsidiary_O American_B-ORG Drug_I-ORG Stores_I-ORG ,_I-ORG Inc._I-ORG was_O formed_O and_O consisted_O of_O American_B-MISC Stores_I-MISC drugstore_O holdings_O of_O Osco_B-ORG Drug_I-ORG ,_O Sav-on_B-PER Drugs_I-PER ,_O the_O Osco_B-LOC side_O of_O the_O Jewel_B-MISC Osco_I-MISC food-drug_O combination_O stores_O and_O RxAmerica_B-ORG ._O"; 
		test 						= "In 1989 , a new subsidiary American Drug Stores , Inc. was formed and consisted of American Stores drugstore holdings of Osco Drug , Sav-on Drugs , the Osco side of the Jewel Osco food-drug combination stores and RxAmerica .";
		patternWithOutVariables1 	= "drugstore holdings of";
		
		Context leftContext5 = new LeftContext(testAnnotated,test, patternWithOutVariables1);
		Context rightContext5 = new RightContext(testAnnotated,test, patternWithOutVariables1);
		
		assertTrue("American Drug Stores , Inc.".equals(leftContext5.getSuitableEntity("http://dbpedia.org/ontology/Legislature")));
		assertTrue("Osco Drug".equals(rightContext5.getSuitableEntity("http://dbpedia.org/ontology/Legislature")));

		// ######################################################################

		testAnnotated				= "with_O the_O head_O of_O the_O slain_O Goliath_B-LOC ,_O as_O he_O is_O in_O Donatello_B-LOC 's_O and_O Verrocchio_B-LOC 's_O statues_O ._O";
		test						= "Michelangelo 's David differs from previous representations of the subject in that the Biblical hero is not depicted with the head of the slain Goliath , as he is in Donatello 's and Verrocchio 's statues .";
		patternWithOutVariables1	="'s and";
		
		
	}
}