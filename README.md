Author: Parth Gupta (pgupta@dsic.upv.es) Date: Jan, 2013.

Version: v 1.0

Add following .jar files in /lib directory.

	* antlr.jar
	* icu4j-4_6.jar
	* Nemo-20130430.jar
	* nlel-20130326.jar
	* terrier-3.5-core.jar
	* trove-3.0.1.jar

Terrier will probably more .jar files. You can safely add all the .jar files from terrier's lib/ to clpd-kbs/lib and add to your build-path.

It contains code used in experiement of the KBS paper for cross-language plagiarism detection. The programs are written for research purpose only. If you have any questions feel free to start an issue on GitHub. The programs are written for research purpose only.

Alberto Barrón-Cedeño, Parth Gupta, Paolo Rosso: Methods for cross-language plagiarism detection. Knowl.-Based Syst. 50: 211-217 (2013)

To Get Started:

1. Expt A with CNG

The programs to run are 
	i)	es.upv.nlel.cng.PrepareCollectionExptA  - Will prepare the character n-gram data
	ii)	es.upv.nlel.cng.CLCNGExptA - Will calculate similarity between source and target documents and generate output file
	iii)	es.upv.nlel.eval.AnalyseRecall -  Measure recall for different types and length of experiments




All rights reserved. Copyright (C) 2011 Parth Gupta
