package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternConfidenceMeasureThread extends Thread {

	private List<PatternMapping> patternMappings = null;
	
	private final NLPediaLogger logger = new NLPediaLogger(PatternConfidenceMeasureThread.class);

	private int patternToMeasure;

	public PatternConfidenceMeasureThread(List<PatternMapping> list) {

		this.patternMappings = list;
		
		for ( PatternMapping pm : this.patternMappings ) {
			
			this.patternToMeasure += pm.getPatterns().size();
		}
	}
	
	@Override
	public void run() {
		
		Map<String,ConfidenceMeasure> confidenceMeasures = ConfidenceMeasureFactory.getInstance().getConfidenceMeasureMap();
		
		// go through all confidenceMeasure
		for ( ConfidenceMeasure confidenceMeasure : confidenceMeasures.values() ) {

			this.logger.info(confidenceMeasure.getClass().getSimpleName() + " started from " + this.getName() +"!");
			
			// and check each pattern mapping
			for (PatternMapping patternMapping : patternMappings ) {
			
				this.logger.debug("Calculation of confidence for mapping: " + patternMapping.getProperty().getUri());
				confidenceMeasure.measureConfidence(patternMapping);
			}
			this.logger.info(confidenceMeasure.getClass().getSimpleName() + " from " + this.getName() + " finished!");
		}
	}
	
	public List<PatternMapping> getConfidenceMeasuredPatternMappings(){
		
		return this.patternMappings;
	}

	public String getProgress() {

		return "0";//NumberFormat.getPercentInstance().format((double) i / (double) patternToMeasure);
	}
}
