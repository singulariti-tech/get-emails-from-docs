# get-email-from-docs

This application extracts emails from documents of various formats including doc, docx, pdf and rtf. 
Although these are the supported formats the code could potentially support other formats too with minor changes.

Although the binary was compiled with Java 19, the code is compatible with Java 8+. So you may have to recompile it 
in case you need it to run on a lower version of Java runtime. 

## Pre-requisites

Java Runtime 19.x.x

## Supported Document Formats

doc, docx, rtf, pdf
Documents of other formats will be ignored.

## Usage

- Copy jar to a folder with execute permissions
- Type the following command

java -jar get-email-from-docs-1.0.jar -s <path-to-directory-with-docs> -o <path-to-output-directory>

## Output

Emails that are extracted will be saved to a file name 'extracted-emails.csv', a comma separated file. Failures 
(documents in which emails were not found or from which emails could not be extracted) will be listed in 'failed.txt'.