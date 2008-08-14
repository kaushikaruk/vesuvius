package sahana.algo.classifiers.bayes;

import sahana.DMAlgorithm;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

public class NaiveBayesSimple extends weka.classifiers.bayes.NaiveBayesSimple
		implements DMAlgorithm
{

	public String mine(String[] args)throws Exception
	{
	    Classifier scheme;
		try
		{
			scheme = new NaiveBayesSimple();
			return Evaluation.evaluateModel(scheme, args).toString();
		} catch (Exception e)
		{
			System.err.println(e.getMessage());
			throw e;
		}
		//return  null;
	}

}
