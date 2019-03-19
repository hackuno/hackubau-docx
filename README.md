# Hackubau Word Generator by template.docx
<span class="lead">
Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility</span>
 
<br/><br/>
Please write to me <hck@hackubau.it> if you have any questions.
<br/><br/>
[![Maven Central](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22)
<br/>
[![Java Docs](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Java%20Docs)](https://hackuno.github.io/hackubau-docx/docs)
<br/><br/>
<pre>
<code>
<a href="https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22"> &#60;&#60; Here you can find gradle, groovy and others package managers entries &#62;&#62;</a>
<br/>
<b> MAVEN pom.xml </b>
<br/>
&#60;dependency&#62;
  &#60;groupId&#62;it.hackubau&#60;/groupId&#62;
  &#60;artifactId&#62;hackubau-docs&#60;/artifactId&#62;
  &#60;version&#62;1.0-RELEASE&#60;/version&#62;
&#60;/dependency&#62;
</code>
</pre>
<br/>



<br/>
<h1><b>What is this?</b></h1>
This is a Service to perform susbstitution of placeholders in .docx files (templates) writing simply something like this in the word template:
<br/><br/>
<kbd> ${yourObject.yourField} </kbd> 
<br/>
<kbd> ${yourPlaceholder} </kbd> 
<br/>
<kbd> ${today} </kbd>
<br/>
<kbd> ${yourObject.yourField.yourEventuallyNestedField} </kbd>
<br/>
<kbd> ${list_yourObject.yourField1@separator#yourField2.nestedField} </kbd>

<b>You can easly pass your custom Object/List of objects and the engine will retrieve everything!</b>
<i>The engine will read the document to find the placeholders and then, for each of them, choose the right object from the provided parameters (objs, listObj or fixedMappings) and invoke the GET methods specified by the placeholder itself </i>
 
<h1 class="lead">Let me see!</h1>
 
 
<h2><b>API - Step by step guide</b></h2>

 <ul style="list-style-type:circle;">
  <li> <a href="#m1">HashMap key-value mappings</a></li>
  <li> <a href="#m2">Object mappings</a></li>
  <li> <a href="#m2.1">Object mappings with personalized identifiers</a></li>
  <li> <a href="#m3">List&#60;Object&#62; mappings with recursively printing</a></li>
  <li> <a href="#m3.1">List&#60;Object&#62; mappings - concatenate fields and set a separator </a></li>
</ul>




<h3 id="m1">(Mode 1) HashMap key-value mappings</h3>

<pre><code>
<b><u>template.docx</u></b>

This is the document of ${name}.
${name} happiness level is: ${happiness}!

</code></pre>
<br/>
<pre><code>
<b><u>java (pseudocode) </u></b>

HashMap&#60;String, String&#62; maps;
maps.put("name","giorgio");
maps.put("happiness","cioppy bau");

docxService.generateDocument(template.docx, maps, output.docx);

</code></pre>
<br/>
<pre><code>
<b><u>out.docx </u></b>

This is the document of Giorgio.
Giorgio happiness level is: cioppi bau!

</code></pre>
<br/>







<h3 id="m2">Mode 2.0) Object mappings </h3>

<pre><code>
<b><u>template.docx</u></b>

This is the document of ${anagrafics.name}.
${anagrafics.name} happiness level is: ${anagrafics.happiness.actualLevel}!

</code></pre>
<br/>
<pre><code>
<b><u>java (pseudocode)</u></b>

//instantiating my object that i want to use in .docx template (it must be extending HckReflect)
Anagrafics giorgio = new Anagrafics();
giorgio.setName("Giorgio");

//creating the giorgio's happiness value - (it is another object so it must be extending HckReflect too if i want to use it in .docx template)
Happiness happy = new Happiness("Cioppy Bau!");

//setting giorgio's happiness property
giorgio.setHappiness(happy);

//preparing my list of objects
List&#60;HckReflect&#62; myObjects = Lists.newArrayList();
myObjects.add(giorgio);

docxService.generateDocument(template.docx, outputFile, <b>myObjects</b> ,null,null)

</code></pre>
<br/>

<pre><code>
<b><u>out.docx</u></b>

This is the document of Giorgio.
Giorgio happiness level is: Cioppi Bau!!

</code></pre>
<br/>








<h3 id="m2.1">Mode 2.1) Object mappings with personalized identifiers </h3>
<p>It become usefull when you have more thant 1 object of the same class to be mapped in the document</p>

<pre><code><b><u>template.docx</u></b>

This is the document of ${father.name}, the father of ${child.name}

</code></pre>
<br/>
<pre><code>
<b><u>java (pseudocode)</u></b>

//instantiating my objects that i want to use in .docx template (it must be extending HckReflect)
Anagrafics fatherObj = new Anagrafics();
Anagrafics childrenObj = new Anagrafics();

fatherObj.setName("Mario");
childrenObj.setName("Robinhood");

//N.B. identifier propery is ereditated by HckReflect, now i am setting a personalized identifier wich will be used in the .docx mapping
fatherObj.setIdentifier("father");
childrenObj.setIdentifier("child");


///preparing my *list of objects*
List&#60;HckReflect&#62; myObjects = Lists.newArrayList();
myObjects.add(fatherObj);
myObjects.add(childrenObj);

docxService.generateDocument(template.docx, outputFile, <b>myObjects</b> ,null,null)

</code></pre>
<br/>
<pre><code>
<b><u>out.docx </u></b>

This is the document of Mario, the father of Robinhood

</code>
</pre>





<h3 id="m3">Mode 3.0) List&#60;Object&#62; mappings with recursively printing</h3>
<p>For example if you have to print the list of someone's childs</p>

<pre><code>
<b><u>template.docx</u></b>

Theese are your childrens:
${list_anagrafics.name}

</code></pre>
<br/>
<pre><code>
<b><u>java (pseudocode)</u></b>

//instantiating my objects that i want to use in .docx template (it must be extending HckReflect)
Anagrafics child1 = new Anagrafics("Giorgio");
Anagrafics child2 = new Anagrafics("Mario");
Anagrafics child3 = new Anagrafics("Pippo");

//now just prepare the List of childs
List&#60;HckReflect&#62; yourChilds = Lists.newArrayList();
yourChilds.add(child1);
yourChilds.add(child2);
yourChilds.add(child3);


//and here preparing my list of *list of objects*
List&#60;HckReflect&#62; myListObjectsList = Lists.newArrayList();
myListObjectsList.add(yourChilds);

docxService.generateDocument(template.docx, outputFile,null, <b>myListObjectsList</b> ,null)

</code></pre>
<br/>
<pre><code>
<b><u>out.docx </u></b>

Theese are your childrens:
Giorgio, Mario, Pippo

</code>
</pre>




<h3 id="m3.1">Mode 3.1) List&#60;Object&#62; mappings - concatenate fields and set a separator </h3>
<p>For example if you have to print the list of someone's childs but you want specificy more fields and choose a personalized separator</p>

<pre><code>
<b><u>template.docx (n.b. "list_", "@" and "#" are keywords)</u></b> 

Theese are your childrens:
${list_anagrafics@\r\n.name#mother.name#mother.surname@-#age}

</code></pre>
<br/>
<pre><code>
<b><u>java (pseudocode)</u></b>

//instantiating my objects that i want to use in .docx template (it must be extending HckReflect)
Anagrafics child1 = new Anagrafics("Giorgio");
Anagrafics child2 = new Anagrafics("Mario");
Anagrafics child3 = new Anagrafics("Pippo");

//Mother class must be extending HckReflect because i want to call it in .docx
child1.setMother(new Mother("Lisa","asiL"));
child2.setMother(new Mother("Unknow","Unknow"));
child3.setMother(new Mother("The crazy one","Many many crazy"));

child1.setAge(10);
child2.setAge(12);
child3.setAge(13);

//now just prepare the List of childs
List&#60;HckReflect&#62; yourChilds = Lists.newArrayList();
yourChilds.add(child1);
yourChilds.add(child2);
yourChilds.add(child3);


//and here preparing my list of *list of objects*
List&#60;HckReflect&#62; myListObjectsList = Lists.newArrayList();
myListObjectsList.add(yourChilds);

docxService.generateDocument(template.docx, outputFile,null, <b>myListObjectsList</b> ,null)

</code></pre>
<br/>
<pre><code>
<b><u>out.docx </u></b>

Theese are your childrens:
Giorgio, Lisa-asiL, 10
Mario, Unknow-Unknow, 12
Pippo, The crazy one-Many many crazy, 13


</code></pre>
<br/>


