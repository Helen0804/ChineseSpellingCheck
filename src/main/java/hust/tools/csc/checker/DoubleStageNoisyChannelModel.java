package hust.tools.csc.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import hust.tools.csc.ngram.NGramModel;
import hust.tools.csc.score.AbstractNoisyChannelModel;
import hust.tools.csc.util.ConfusionSet;
import hust.tools.csc.util.Dictionary;
import hust.tools.csc.util.Sentence;
import hust.tools.csc.wordseg.AbstractWordSegment;

public class DoubleStageNoisyChannelModel extends AbstractNoisyChannelModel {
	
	private Dictionary dictionary;
	private ConfusionSet confusionSet;
	private NGramModel nGramModel;
	private AbstractWordSegment wordSegment;
	private final int order = 3;
	private final int beamSize = 150;
 
	public DoubleStageNoisyChannelModel(Dictionary dictionary, NGramModel nGramModel, ConfusionSet confusionSet,
			AbstractWordSegment wordSegment) throws IOException {
		this.nGramModel = nGramModel;
		this.confusionSet = confusionSet;
		this.dictionary = dictionary;
		this.wordSegment = wordSegment;
	}
	
	@Override
	public ArrayList<Sentence> getCorrectSentence(Sentence sentence) {
		ArrayList<Sentence> candSens = new ArrayList<>();
		ArrayList<Integer> locations = new ArrayList<>();
		
		////////////////////////////////基于bigram匹配的检错
		locations = getErrorLocationsBySIMD(dictionary, sentence);
		//连续单字词的最大个数小于2，不作处理直接返回原句
		if(locations.size() > 1) {
			candSens = beamSearch(dictionary, confusionSet, beamSize, sentence, locations);
			return candSens;
		}else {
			candSens.add(sentence);
		}
		
		////////////////////////////////基于分词的检错
		sentence = candSens.get(0);
		ArrayList<String> words = wordSegment.segment(sentence);
		if(words.size() < 2) {//分词后，词的个数小于2的不作处理，不作处理直接返回原句
			return candSens;
		}
		locations = locationsOfSingleWords(words);
		//连续单字词的最大个数小于2，不作处理直接返回原句
		if(locations.size() > 1) {
			candSens = new ArrayList<>();
			candSens = beamSearch(dictionary, confusionSet, beamSize, sentence, locations);
			return candSens;
		}	

		return candSens;
	}

	@Override
	public double getSourceModelLogScore(Sentence candidate) {
		return nGramModel.getSentenceLogProb(candidate, order);
	}

	@Override
	public double getChannelModelLogScore(Sentence candidate) {
		return 1.0;
	}	
}

