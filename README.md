# Hackubau Word Generator by template.docx
<span class="lead">
Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility</span>
 
<br><br>
Please write to me <hck@hackubau.it> if you have any questions.
<br><br>
[![Maven Central](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22)
<br>
[![Java Docs](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Java%20Docs)](https://hackuno.github.io/hackubau-docx/docs)
<br><br>
<pre>
<code>
<a href="https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22"--Here you can find gradle, groovy and others package managers entries--</a>
<br>
<b> MAVEN pom.xml </b>
<br>
&#60;dependency&#62;
  &#60;groupId&#62;it.hackubau&#60;/groupId&#62;
  &#60;artifactId&#62;hackubau-docs&#60;/artifactId&#62;
  &#60;version&#62;1.0-RELEASE&#60;/version&#62;
&#60;/dependency&#62;
</code>
</pre>
<br>



<br>
<h1><b>What is this?</b></h1>
This is a Service to perform susbstitution of placeholders in .docx files (templates) writing simply something like this in the word template:
<br><br>
<kbd> ${yourObject.yourField} </kbd> 
<br>
<kbd> ${yourPlaceholder} </kbd> 
<br>
<kbd> ${today} </kbd>
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


<h1 class="lead">API specifications</h1>

<h4><b><u> MAIN FUNCTIONS: </u></b></h4>
<br>
<p style="font-size:2px;">generateDocument(File template, HashMap&#60;String, String&#62; replace, outputFile)</p>
<p> generateDocument(File template, File out, List&#60;? extends HckReflect&#62; objs, List&#60;List&#60;? extends HckReflect&#62;&#62;listsObj, HashMap&#60;String, String&#62; fixedMappings)</p>
<i>This will read the document to find the placeholders and then, for each of them, choose the right object from the provided parameters (objs, listObj or fixedMappings) and invoke the GET methods specified by the placeholder itself </i>
<BR><BR>
 
 
<h2><b> How does it work? Step by step guide</b></h2>


<h3>1)Simple HashMap key-value substitution </h3>

<CODE>
<pre><b><u>template.docx</u></b>

This is the document of ${name}.
${name} happiness level is: ${happiness}!

</pre></CODE><br><CODE><pre>
<b><u>java (pseudocode) </u></b>

HashMap&#60;String, String&#62; maps;
maps.put("name","giorgio");
maps.put("happiness","cioppy bau");

docxService.generateDocument(template.docx, maps, output.docx);

</pre></CODE><br><CODE><pre>
<b><u>out.docx </u></b>

This is the document of Giorgio.
Giorgio happiness level is: cioppi bau!

</pre>
</CODE>

<h3>2)Simple Object GET-METHODS mappings </h3>

<CODE>
<pre><b><u>template.docx</u></b>

This is the document of ${anagrafics.name}.
${anagrafics.name} happiness level is: ${anagrafics.happiness.actualLevel}!

</pre></CODE><br><CODE><pre>
<b><u>java (pseudocode) (n.b. the classes must extends HckReflect) </u></b>

//instantiating my object that i want to use in .docx template (it must be extending HckReflect)
Anagrafics giorgio = new Anagrafics();

//creating the giorgio's happiness value - (it is another object so it must be extending HckReflect too if i want to use it in .docx template)
Happiness happy = new Happiness("Cioppy Bau!");

//setting giorgio's happiness property
giorgio.setHappiness(happy);

///preparing my list of objects
List&#60;HckReflect&#62; myObjects = Lists.newArrayList();
myObjects.add(giorgio);

docxService.generateDocument(template.docx, outputFile, <b>myObjects</b> ,null,null)

</pre></CODE><br><CODE><pre>
<b><u>out.docx </u></b>

This is the document of Giorgio.
Giorgio happiness level is: Cioppi Bau!!

</pre>
</CODE>


