package sahana.algo.classifiers.bayes;

import sahana.DMAlgorithm;

public class NaiveBayesMultinomial extends
		weka.classifiers.bayes.NaiveBayesMultinomial implements DMAlgorithm
{

	public String mine(String[] args)throws Exception
	{
		try
		{
			return weka.classifiers.Evaluation.evaluateModel(
					new NaiveBayesMultinomial(), args).toString();
		} catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw e;
		}
		//return null;
	}

}
