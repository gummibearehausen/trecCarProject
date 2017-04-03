## TREC Car - Data Science Code Submission 3

**submission URL:** [Link] (https://github.com/gummibearehausen/trecCarProject.git)

The code is written in Java as a Maven project. The user required to compile the project the command:

#### After cloning the repository:

$ cd trecCarIndex

$ mvn package assembly:single

$ java -jar ./target/treccar-archecture-framework-1.4-jar-with-dependencies.jar  *<paragraph>* *<outlines>* *<qrels>* *<output>*:x


This command index the paragraphs and save them in a local directory:  *indexfile*

runfile also will be generated and put in the folder \tempSearchResult
#### runfile format:
QueryId+"\t"+0+"\t"+ParagraphId +"\t"+docRank+"\t"+1.0/docRank+"\t"+"BBTeam"+"\n";


# Contributers
Bryan Zhang, Bahram Behzadian
