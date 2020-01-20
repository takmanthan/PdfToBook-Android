 # PdfToBook-Android
 This project convert PDF URL to view pager looking like book.
 ##  How to
 To get a Git project into your build:

 ### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
### Step 2. Add the dependency

```
dependencies {
	        implementation 'com.github.takmanthan:PdfToBook-Android:0.1.0'
	}
```  
### step 3. Add this code in your code where you want to open your book.
```
// Create Your Directory 
PDFUtils.downloadDirectory = "Your_directory_name";

//put your .pdf url
String url = "https://www.tutorialspoint.com/cprogramming/cprogramming_tutorial.pdf"; 

//use this same line for open PDF
//this = context
//url = pdf url
PDFUtils.openPdfBook(this,url);
```

  
