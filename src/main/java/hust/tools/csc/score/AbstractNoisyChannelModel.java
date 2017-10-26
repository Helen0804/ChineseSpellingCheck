package hust.tools.csc.score;

import hust.tools.csc.ngram.NGramModel;
import hust.tools.csc.util.Sentence;

/**
 *<ul>
 *<li>Description: 噪音通道模型抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年10月24日
 *</ul>
 */
public abstract class AbstractNoisyChannelModel implements NoisyChannelModel {
	
	protected NGramModel nGramModel;
	
	public AbstractNoisyChannelModel() {

	}
	
	/**
	 * 返回候选句子noisy channel model：p(s|c)*p(c)中的p(c)
	 * @param candidate	候选句子
	 * @return	p(c)
	 */
	public double getSourceModelLogScore(Sentence candidate) {
		return nGramModel.getSentenceLogProb(candidate , nGramModel.getOrder());
	}
	
	/**
	 * 返回noisy channel model：p(s|c)*p(c)中的p(s|c)
	 * @param candidate	候选句子
	 * @return	p(s|c)
	 */
	public abstract double getChannelModelLogScore(Sentence candidate);
}