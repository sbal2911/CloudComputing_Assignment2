# Assignment 2: Document Similarity using MapReduce

**Name: Sudeepta Bal** 

**Student ID: 801455628** 

## Approach and Implementation

### Mapper Design
The Mapper design takes in account of each and every lines of the input text file. Here, the key is considered to be the byte offset while the value is considered to be the lines of every document comprising of a DocumentID and its respective text content. The logic then takes out the DocumentID and perfoms the operation of converting the entire text content to lowercase and cleans the punctuation marks, keeping only the unique words. So all the unique-words in the document are now leveraged by the Mapper logic to give a key-value pair where each unique word serves as the key and the DocumentID serves as the value. Overall, the process aims at creating an index file that shows all the documents which contains the extracted unique words. This is in turn proves to be helpful for the Reducer to quickly recognize the documents sharing words and thus Jaccard Similarity is computed.

### Reducer Design
After Mapper does its work, the Reducer logic finally gets a word in form of a key and a lineup of DocumentIDs in form of values, which means these are documents that contain the word. For every word, it modifies a map that links every document with the set of specific words it carries. During the cleanup logic, it originates all distinct document pairs for which the commonality and the union of word content are calculated. Then, Jaccard similarity is applied i.e., the ratio of intersected words with that of complete distinct words. At last, the logic results in giving a similarity score for each document pair as per the required format, having uniform ordering and arranged outcome/results.

### Overall Data Flow

In the MapReduce processes, the input is considered to be a group of documents, in which every line has a DocumentID and its corresponding text content. The Mapper logic handles every document, removes the punctuations and converts all the content to lower-case characters, gets the distict words and frames key-value pairs, in which the value is considered to be the DocumentID and the key is the word. In the shuffle and sort phase, Hadoop aggregates every values(DocumentIDs) together, on its own, based upon the same word it carries, making a point to give each reducer each and every document associated with a specific word. The Reducer logic then reassembles the group of words associated with each document, analyzes each document pair, and calculates the Jaccard Similarity by computing the commonality and union of their word sets. At last, the reducer generates an outcome of each document pair together with its similarity score, as per the asked format.

---

## Setup and Execution
The logic for the DocumentSimilarityDriver class, DocumentSimilarityMapper class and DocumentSimilarityReducer class are added and can be viewed from the repository files. After the logic was updated, the following commands were executed in Terminal from Github Codespace.

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp shared-folder/input/data/input.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command:

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/DocumentSimilarity-0.0.1-SNAPSHOT.jar com.example.controller.DocumentSimilarityDriver /input/data/input.txt /output1
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output1/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output1 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output/
    ```

## Execution with a single data node

Since we also, have to run this project using single data node, the steps for execution remains the same as stated above from **steps 1 to 10 (sub-point 1).**

However, with regards to **step 10 and sub-point 2, there will be a slight change** in the output folder's name as **output.txt is already generated** from the execution with 3 data nodes, so for a new output folder to store your results, use the below Docker command to copy from container to local machine
```bash
exit 
```
```bash
docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output2/
```

## Challenges and Solutions

**Problem:** After adding the necessary logic for DocumentSimilarityDriver class, DocumentSimilarityMapper class and DocumentSimilarityReducer class, I executed the code but I received a **ClassNotFoundException** while I was implementing step 8 of the code run.

**Solution:** Earlier, I executed the code with my Driver class located in the path **"/workspaces/CloudComputing_Assignment2/src/main/com/example/controller/DocumentSimilarityDriver.java"**, but Maven expects JAVA source files to be under **"src/main/java"**. Hence, I corrected my path to **"/workspaces/CloudComputing_Assignment2/src/main/java/com/example/controller/DocumentSimilarityDriver.java"** and it executed successfully without any errors.

---
## Sample Input

**Input from `small_dataset.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```

## Created Input:

**Input from `input.txt`, used while executing the code**

My input dataset consists of **3 documents (Document1, Document2 and Document3)** where **Document1 consists of 1024 words**, **Document2 consists of 3245 words** and **Document3 consists of 4378 words.** The input dataset is quite long and hence, it can be reviewed directly by visiting the repository files under the folder "**shared-folder/input/data/input.txt**"

## Obtained Output:

**Output generated from executing the code**

The output file named **output.txt** were the results captured, on executing with **3 data nodes.** The output file named **output2.txt** were the results captured, on executing with **1 data node.**
```
"Document1, Document2 Similarity: 0.07"
"Document1, Document3 Similarity: 0.06"
"Document2, Document3 Similarity: 0.25"
```

## Observations from running the project on 3 data nodes and a single data node:

As per the instructions of the assignment, experiment with 3 data nodes and a single data node were carried out. So, the execution with 3 data nodes completed faster compared to 1 data node, as tasks were divided amongst all the 3 nodes hence lowering the overall time for execution. However, with 1 data node, all calculations and storage were taken care by a single node. 

However, for this project specifically, **a very minimal time difference was observed in between 2 experiments (one using 3 data nodes and the other one using 1 data node).** It also depends on the input size you are passing, the more the size of the input, the difference observed will be more prominent. In any which way, **the experiment with 3 data nodes proves to be more efficient for performing the task compared to the one with single data node.**

The first project run was executed with 3 data nodes and then with single data node, hence the **docker-compose.yml** consists of latest data i.e., **single data node configuration.**

---