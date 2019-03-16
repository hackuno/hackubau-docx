# Hackubau Word Generator by template.docx
<span class="lead"><b><maven: hackubau-docx></b> Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility</span>

<br>
<h1><b>What is this?</b></h1>
This is a Service to perform susbstitution of placeholders in .docx files (templates) writing simply something like this in the word template:
<br><BR><BR>
<code> ${yourObject.yourField} </code> <i> You have just to pass to the service params a List containing &#60;YourObject extends HckReflect&#62;</i>
<br>
<code> ${yourPlaceholder} </code> <i> passing in params a HashMap&#60;String,String&#62;</i>
<br>
<code> ${today} </code> <i> special keyword</i>
<br>
<code> ${yourObject.yourField.yourEventuallyNestedField} </code>  <i> passing in params a List&#60;? extends HckReflect&#62;</i>
<br>
<code> ${list_yourObject.yourField1@separator#yourField2.nestedField} </code><i> passing a list containing a <i>List&#60;? extends HckReflect&#62;</i>
<h5>Where:</h5
 
 <kbd><u>yourObject</u></kbd> 
  <p class="lead">&#62;&emsp;Your CustomObject className (must be extending HckReflect)</p>
 <code>	<i>(es. Dogs.class -> dogs)</i></code>	
  <p>&emsp;&emsp;or<p>
  <p class="lead">&#62;&emsp;Your CustomObject identifier property (ereditated from abstract class HckReflect) for every passed object</p>
  <code><i>(Es. Dogs d = new Dogs(); d.setIdentifier("customName")</i></code>
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
 
<b>You can easly pass your custom Object/List of objects and the engine will retrieve everything!</b>


<h1 class="lead">Case of use</h1>

<h4 class="lead">Do you have to create Word(.docx) files starting by a template and substituting some placeholders with right values?
This tool will make it so easy that you will be stunned! </h4>

<h1 class="lead">Hey, but i need an ad easy way to retrieve my Objects properties and place them in my .docx files without write a line of code!</h1>

<h4 class="lead">You will have just to make your objects extending my HckReflect.class and pass them all to my service!</h4>
<h5 class="lead">The remaining is just about .docx template! Demand it to your customers.</h5>

