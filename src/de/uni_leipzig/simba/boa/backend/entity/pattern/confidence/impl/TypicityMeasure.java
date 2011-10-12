/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.impl.IterationCommand;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.ConfidenceMeasure;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * @author Daniel Gerber
 *
 */
public class TypicityMeasure implements ConfidenceMeasure {

	private final NLPediaLogger logger					= new NLPediaLogger(TypicityMeasure.class);
	private NamedEntityRecognizer ner;
	private final int maxNumberOfEvaluationSentences 	= Integer.valueOf(NLPediaSettings.getInstance().getSetting("maxNumberOfTypicityConfidenceMeasureDocuments"));
	
	private PatternSearcher patternSearcher;
	
	// used for sentence segmentation
	private Reader stringReader;
	private DocumentPreprocessor preprocessor;
	private StringBuilder stringBuilder;
	private static final Map<String,String> BRACKETS = new HashMap<String,String>();
	static {
		
		BRACKETS.put("-LRB-", "(");
		BRACKETS.put("-RRB-", ")");
		BRACKETS.put("-LQB-", "{");
		BRACKETS.put("-RQB-", "}");
	}
	
	public TypicityMeasure() {}
	
	/* (non-Javadoc)
	 * @see simba.nlpedia.entity.pattern.evaluation.PatternEvaluator#evaluatePattern(simba.nlpedia.entity.pattern.PatternMapping)
	 */
	@Override
	public void measureConfidence(PatternMapping mapping) {
		
		long start = new Date().getTime();
		
		if ( this.patternSearcher == null ) {
			
			try {
				
				this.patternSearcher = new PatternSearcher(NLPediaSettings.getInstance().getSetting("sentenceIndexDirectory"));
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String domainUri	= mapping.getProperty().getRdfsDomain();
		String rangeUri		= mapping.getProperty().getRdfsRange();
		
		double domainCorrectness;
		double rangeCorrectness;
		
		String nerTagged;
		String segmentedFoundString;
		String segmentedPattern;
		
		Context leftContext;
		Context rightContext;
		
		if ( this.ner == null ) this.ner = new NamedEntityRecognizer();

		for (Pattern pattern : mapping.getPatterns()) {
			
			if ( !pattern.isUseForPatternEvaluation() ) continue;
			
			try {
			
				boolean beginsWithDomain = pattern.isDomainFirst();
				String patternWithOutVariables = this.segmentString(pattern.getNaturalLanguageRepresentationWithoutVariables());
				
				final List<String> sentences = new ArrayList<String>(patternSearcher.getExactMatchSentences(patternWithOutVariables, maxNumberOfEvaluationSentences));
				
				double correctDomain	= 0;
				double correctRange		= 0;
				
				for (String foundString : sentences.size() >= this.maxNumberOfEvaluationSentences ? sentences.subList(0,this.maxNumberOfEvaluationSentences - 1) : sentences) {
					
					nerTagged = this.ner.recognizeEntitiesInString(this.replaceBrackets(foundString));
					segmentedFoundString = this.segmentString(foundString);
					segmentedPattern = this.segmentString(patternWithOutVariables);
					
					if ( nerTagged != null && segmentedFoundString != null && segmentedPattern != null &&
							nerTagged.length() > 0 && segmentedFoundString.length() > 0 && segmentedPattern.length() > 0 ) {
						
						try {
							
							leftContext = new LeftContext(nerTagged, segmentedFoundString, segmentedPattern);
							rightContext = new RightContext(nerTagged, segmentedFoundString, segmentedPattern);
							
							if ( beginsWithDomain ) {
								
								if ( leftContext.containsSuitableEntity(domainUri) ) {
									
									correctDomain += (1D / (double)leftContext.getSuitableEntityDistance(domainUri));
								}
								if ( rightContext.containsSuitableEntity(rangeUri) ) {
									
									correctRange += (1D / (double)rightContext.getSuitableEntityDistance(rangeUri));
								}
							}
							else {
								
								if ( leftContext.containsSuitableEntity(rangeUri) ) {
									
									correctRange += (1D / (double)leftContext.getSuitableEntityDistance(rangeUri));
								}
								if ( rightContext.containsSuitableEntity(domainUri) ) {
									
									correctDomain += (1D / (double)rightContext.getSuitableEntityDistance(domainUri));
								}
							}
						}
						catch ( IndexOutOfBoundsException ioob ) {
							//ioob.printStackTrace();
							this.logger.error("Could not create context for string " + segmentedFoundString + ". NER tagged: " + nerTagged + " pattern: "  + patternWithOutVariables);
						}
						catch (NullPointerException npe) {
							
							this.logger.error("IOExcpetion", npe);
						}
					}
				}
			
				domainCorrectness = (double) correctDomain / (double) sentences.size();
				rangeCorrectness = (double) correctRange / (double) sentences.size();
				
				double typicity = 0D;
				
				typicity = (domainCorrectness + rangeCorrectness) / (2D);//* (double) sentences.size());
				typicity = Double.isNaN(typicity) ? 0d : typicity * (double) (Math.log((int)(sentences.size() + 1)) / Math.log(2));
				
				pattern.setTypicity(typicity);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				this.logger.error("IOExcpetion: ", e);
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				this.logger.error("ParseException: ", e);
			}
			catch (NullPointerException npe) {
				
				this.logger.error("NullPointerException: ", npe);
			}
			catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
				
				this.logger.error("ArrayIndexOutOfBoundsException: ", aioobe);
			}
		}
		this.logger.info("Typicity measuring for pattern_mapping: " + mapping.getProperty().getUri() + " finished in " + (new Date().getTime() - start) + "ms.");
	}
	
	private String replaceBrackets(String foundString) {

		for (Map.Entry<String, String> bracket : TypicityMeasure.BRACKETS.entrySet()) {
			
			if ( foundString.contains(bracket.getKey())) {
	
				foundString = foundString.replace(bracket.getKey(), bracket.getValue());
			}
		}
		return foundString;
	}

	public static void main(String[] args) {

		String s = "Microsoft is a company located in Redmond.";
	}
	private String segmentString(String sentence) {
		
		try {
			
			this.stringReader = new StringReader(sentence);
			this.preprocessor = new DocumentPreprocessor(stringReader,  DocumentPreprocessor.DocType.Plain);
			
			Iterator<List<HasWord>> iter = this.preprocessor.iterator();
			while ( iter.hasNext() ) {
				
				stringBuilder = new StringBuilder();
				
				for ( HasWord word : iter.next() ) {
					stringBuilder.append(word.toString() + " ");
				}
				return stringBuilder.toString().trim();
			}
		}
		catch (ArrayIndexOutOfBoundsException aioobe) {
			
			logger.debug("Could not segment string...", aioobe);
		}
		return "";
	}
}
