# Hackubau Word Generator by template.docx
<span class="lead">
 [![Maven Central](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22)
 <b> Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility </b></span>

<a class="" href="https://hackuno.github.io/hackubau-docx/docs">Documentation JavaDocs Here</a>

<br>
<h1><b>What is this?</b></h1>
This is a Service to perform susbstitution of placeholders in .docx files (templates) writing simply something like this in the word template:
<br><br>
<kbd> ${yourObject.yourField} </kbd> 
<br>
<kbd> ${yourPlaceholder} </kbd> 
<br>
<kbd> ${today} </kbd> <i> special keyword</i>
<br>
<kbd> ${yourObject.yourField.yourEventuallyNestedField} </kbd>
<br>
<kbd> ${list_yourObject.yourField1@separator#yourField2.nestedField} </kbd>

<b>You can easly pass your custom Object/List of objects and the engine will retrieve everything!</b>

<center> &#62; Check the detailed GUIDE scrolling down. &#60; </center> 
 
<h1 class="lead">When is it usefull?</h1>

<h4 class="lead">Do you have to create Word(.docx) files starting from a template and substituting custom placeholders with right values?<br>
This tool will make it so easy that you will be stunned! </h4>

<sample>Hey, but i need an ad easy way to retrieve my Nested Objects properties, List of Objects and String values and place them together in my .docx files without write a line of code!</sample>
<h4 class="lead">You will have just to make your objects extending my HckReflect.class and pass them all to my service!</h4>
<h5 class="lead">The remaining is just about .docx template! Demand it to your customers.</h5>


<br><br>
Please write to me <hck@hackubau.it> if you have any questions.
<br>
[![Maven Central](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22)
<br>
<pre>
<code>
&#60;dependency&#62;
  &#60;groupId&#62;it.hackubau&#60;/groupId&#62;
  &#60;artifactId&#62;hackubau-docs&#60;/artifactId&#62;
  &#60;version&#62;1.0-RELEASE&#60;/version&#62;
&#60;/dependency&#62;
</code>
</pre>
<br>

<h1 class="lead">API specifications</h1>

<h4><b><u> MAIN FUNCTIONS: </u></b></h4>
<br>
<kbd>generateDocument(File template, HashMap&#62;String, String&#60; replace, String outputDocument) - return: The Generated File</kbd>
 <i>The simplest way: This will just replace the placeholders <b>${key}</b> found in the .docx with the <b> value </b> mapping provided by the hashMap</i>
 
 <kbd>
 generateDocument(File template, File out, List&#62;? extends HckReflect&#60; objs, List&#62;List&#62;? extends HckReflect&#60;&#60;listsObj, HashMap&#62;String, String&#60; fixedMappings) - return: The Generated File 
</kbd>
<i>The best way: This will read the document to find the placeholders and then, for each of them, choose the right object from the provided parameters (objs, listObj or fixedMappings) and invoke the GET methods specified by the placeholder itself </i>

<h4><b> Details </b></h4>
<br>
<code><u> yourObject identifier - what you have to use into the .docx file as placeholder to invoke a specific object</u></code><br>
HckReflect class have a property named identifier.<br>
If you set a value to it, the final identifier of <kbd>yourObject</kbd> will become this <u>identifier</u>, else it will be the <u>CLASS NAME</u>.<br>
<br>

<code><u>List&#60;? extends HckReflect&#62; objs </code></u>
The list of single objects used in the mappings.
<br>
The engine will understand by "yourObject identifier" who is the right object and will retrieve values from GET methods of theese objects following placeholder instructions.
<br>

<code><u> List&#60;List&#60;? extends HckReflect&#62;&#62; objs - in </u></code><br>
The list of multiple objects used in the mappings<br>
Same as single object but in the .docx file you will prepend the "list_" keyword so the engin will seek for the first List containing a collection of "yourObject identifier" objects and recursively print them GET methods provided by placeholder.
<br>

<code><u>HashMap&#60;String, String&#62; fixedMappings</code></u>
The list of key-value that egine will use as-is to replace placeholders that do not match any Object-conditions.
 
 
<h4><b><u> SAMPLE </u></b></h4>

<h4><b> Assuming your custom objects are the following: </b></h4>

<code><pre>
 public class People extends HckReflect {
  private String name;
  private String surname;
  private Address address;
  [...constructors, getters and setters ...]
 }</pre>
</code>


<code>
   <pre>
 public class Address extends HckReflect {
  private String completeAddress;
  private String city;
 
  [...constructors, getter and setters ...]
 }</pre>
</code>
<br>
<h4><b> You have just to invoke my service as follow: </b></h4>
<br>
<code>
 DocxService service = new DocxService();
 
 //your .docx template
 File template = [...]
 File fout = new File(...); 
 
 fout = serv.generateDocument(template, fout, Lists.newArrayList(myobj1,myobj2) obj, List<myObjectList1,myObjectList2> listObj, myFixedValues);

 </code>


 <h5>Glossary:</h5>
 <kbd><u>yourObject</u></kbd> 
  <br><span class="lead">&#62;&emsp;Your CustomObject className (must be extending HckReflect)</span><code>	<i>(es. Dogs.class -> dogs)</i></code><br>
  <span class="lead">&#62;&emsp;...or Your CustomObject identifier property (ereditated from abstract class HckReflect) for every passed object </span><code><i>(Es. Dogs d = new Dogs(); d.setIdentifier("customName")</i></code>
 <br><br>
 <kbd><u>list_yourObject</u></kbd> 
  <p class="lead">&#62;&emsp;Same as yourObject but will search for List<yourObject> and recursively print all of them</p>
   <br>
 <kbd><u>yourPlaceholder</u></kbd> 
  <p class="lead">&#62;&emsp;Search the value in provided HashMake<key,value></p>
 <br>
 <kbd><u>today</u></kbd> 
  <p class="lead">&#62;&emsp;today date</p>
 <br>
 <kbd><u>yourField</u></kbd> 
<p>&#62;&emsp;Every getMethod names without "get" word. (ex. getName() -\> yourObject.name)
  <br><br>
 <kbd><u>yourField2.nestedField</u></kbd> 
<p>&#62;&emsp;Yes, you can go recursively to every nested <Object extends HckReflect> fields!
  <br><br>

