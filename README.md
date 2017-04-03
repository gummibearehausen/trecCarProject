## TREC Car - Data Science Code Submission 3
**submission URL:** [Link] (https://github.com/gummibearehausen/trecCarProject.git)
## Cluster-ranking Method:

The code is written in Java as a Maven project. The user required to compile the project the command:

#### After cloning the repository:

$ cd trecCarIndex

$ mvn package assembly:single

$ java -jar ./target/treccar-archecture-framework-1.4-jar-with-dependencies.jar  *paragraph_DIR* *outlines_DIR* *qrels_DIR* *output_DIR*


This command index the paragraphs and save them in a local directory:  *indexfile*

runfile also will be generated and put in the folder \tempSearchResult
#### runfile format:
QueryId+"\t"+0+"\t"+ParagraphId +"\t"+docRank+"\t"+1.0/docRank+"\t"+"BBTeam"+"\n";


## Knowledge base/Dictionary derivation
This pathon code is to derive a knowledge base from the entire Wikipedia article collection. running from command line: argument 1: wikicorpus cbor file 
argument 2: name of the output knowledge base dictionary name 
argument 3: name of the output knowledge base graph name 
argument 4: number of wikipedia articles from corpus you would like to process. if argv[4] == null, the default is the entire wikipedia articlue collection.

pages filtered out: articles on category, image, template, Wikipedia: system files such as deletion, List of.., File:...

knowledge base dictionary:
If the entity's name is same as the surface textual form: then it is not written in the knowledge base.
If the surface form of the entity is different from the entity then [key]:[value] = [surface form (lower case)][entity]
If the Wikipedia page is redirected to another entity: [key]:[value] = [knowledge base entity(lower case)][redirecting entity]
