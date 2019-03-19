# Hackubau Word Generator by template.docx
<span class="lead">
Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility</span>
 
<br/><br/>
Please write to me <hck@hackubau.it> if you have any questions.
<br/><br/>
[![Maven Central](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22it.hackubau%22%20AND%20a:%22hackubau-docs%22)
<br/>
[![Java Docs](https://img.shields.io/maven-central/v/it.hackubau/hackubau-docs.svg?label=Java%20Docs)](https://hackuno.github.io/hackubau-docx/docs)

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
<h5><i><u>It is &nbsp;&nbsp;<b>STRONGLY RECOMMENDED</b>&nbsp;&nbsp; to follow EVERY steps starting by step 1 (it will take just about 5 min, seriously!)</u></i></h5>
<h5><i>I recommend this just because the guide have been wrote as a lessons-pack with growing-complexity-concepts.</i></h5>
 <ol>
  <li> <a href="#m0">Today keyword</a></li>
  <li> <a href="#m1">HashMap key-value mappings</a></li>
  <li> <a href="#m2">Object mappings</a></li>
  <li> <a href="#m2.1">Object mappings with personalized identifiers</a></li>
  <li> <a href="#m3">List&#60;Object&#62; mappings with recursively printing</a></li>
  <li> <a href="#m3.1">List&#60;Object&#62; mappings - concatenate fields and set a separator </a></li>
  <li> <a href="#m4">Just modality randomly mixed together, not really important to see, it'sjust if you need more brain complexity.</a></li>
</ol>


<h3 id="m0">(0) Today keyword</h3>
This is just a real banality.
<pre><code><b><u>template.docx</u></b>
Today is the ${today}.
</code></pre>
<pre><code>
<b><u>java (pseudocode) </u></b>
//just invoke the service - today is a coded value used for the italian pizza-mario date
docxService.generateDocument(template.docx, output.docx, null,null,null);</code></pre>
<pre><code>
<b><u>out.docx </u></b>
Today is the 19/03/2019</code></pre>
<br/>




<h3 id="m1">(Modality 1) HashMap key-value mappings</h3>

<pre><code><b><u>template.docx</u></b>
This is the document of ${name}.
${name} happiness level is: ${happiness}!
</code></pre>
<pre><code>
<b><u>java (pseudocode) </u></b>
HashMap&#60;String, String&#62; maps;
maps.put("name","giorgio");
maps.put("happiness","cioppy bau");

docxService.generateDocument(template.docx, maps, output.docx);
</code></pre>
<pre><code><b><u>out.docx </u></b>
This is the document of Giorgio.
Giorgio happiness level is: cioppi bau!
</code></pre>

<h3 id="m2">(Modality 2.0) Object mappings </h3>

<pre><code><b><u>template.docx</u></b>
This is the document of ${anagrafics.name}.
${anagrafics.name} happiness level is: ${anagrafics.happiness.actualLevel}!
</code></pre>
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

<pre><code><b><u>out.docx</u></b>
This is the document of Giorgio.
Giorgio happiness level is: Cioppi Bau!!
</code></pre>

<h3 id="m2.1">(Modality 2.1) Object mappings with personalized identifiers </h3>
<p>It become usefull when you have more thant 1 object of the same class to be mapped in the document</p>

<pre><code><b><u>template.docx</u></b>
This is the document of ${father.name}, the father of ${child.name}
</code></pre>
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
<pre><code><b><u>out.docx </u></b>
This is the document of Mario, the father of Robinhood
</code>
</pre>





<h3 id="m3">(Modality 3.0) List&#60;Object&#62; mappings with recursively printing</h3>
<p>For example if you have to print the list of someone's childs</p>

<b>Special Keywords glossary:</b>
<pre><code><b>list_</b>
  Place this keyword before your object identifier.
  It will say to the engine that we want to print a list of objects.
</code></pre>

<pre><code><b><u>template.docx</u></b>
Theese are your childrens:
${list_anagrafics.name}
</code></pre>
<pre><code><b><u>java (pseudocode)</u></b>

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
</code></pre><pre><code><b><u>out.docx </u></b>
Theese are your childrens:
Giorgio, Mario, Pippo
</code>
</pre>





<h3 id="m3.1">(Modality 3.1) List&#60;Object&#62; mappings - concatenate fields and set a separator </h3>
<p>For example if you have to print the list of someone's childs but you want specificy more fields and choose a personalized separator</p>

<b>Special Keywords glossary:</b>
<pre><code><b>list_</b>
  Place this keyword before your object identifier.
  It will say to the engine that we want to print a list of objects.

<b>#</b>
  When you are dealing with lists, you can ask the engine to print multiple field for every single record.
  This is the placeholder that you have to place between every field you want to print.
  
<b>@</b>
  This is the separator keyword. 
  It say to the engine what char you want as separator. 
  Place it after the field you want to retrieve and write your separator chars after it
  If you don't specify a separator, it use the default one's.
</code></pre>
 
 
<pre><code><b><u>template.docx</u></b> 
Theese are your childrens:
${list_anagrafics@\r\n.name#mother.name#mother.surname@-#age}
</code></pre>
<pre><code><b><u>java (pseudocode)</u></b>

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
<pre><code><b><u>out.docx </u></b>
Theese are your childrens:
Giorgio, Lisa-asiL, 10
Mario, Unknow-Unknow, 12
Pippo, The crazy one-Many many crazy, 13
</code></pre>

<h5>Of course you can mix up as many lists , objects and key-values as you wany</h5>



<h3 id="m4">(Modality 4) HardCore mode - all mixed together very very randomly!</h3>

<b>Special Keywords glossary:</b>
<pre><code><b>list_</b>
  Place this keyword before your object identifier.
  It will say to the engine that we want to print a list of objects.

<b>#</b>
  When you are dealing with lists, you can ask the engine to print multiple field for every single record.
  This is the placeholder that you have to place between every field you want to print.
  
<b>@</b>
  This is the separator keyword. 
  It say to the engine what char you want as separator. 
  Place it after the field you want to retrieve and write your separator chars after it
  If you don't specify a separator, it use the default one's.
</code></pre>
 
 
<pre><code><b><u>template.docx</u></b> 
Today is ${today}.

In my hashmap cioppy stay for ${cioppy}

Your city is ${you.address.city}

I think that ${Anagrafics.favouriteGame.fancyName} is one of the best game evvah!

Theese are the best words of the world:
${list_bestWords@\r\n.description@---#author}

Theese are the children of ${father.name}:
${list_anagrafics@\r\n.name#mother.name#mother.surname@-#age}
</code></pre>
<pre><code><b><u>java (pseudocode)</u></b>

//instantiating all the objects that i want to use in .docx template (them must be extending HckReflect)
Anagrafics me = new Anagrafics("me");
Anagrafics you = new Anagrafics("Jonny");
Anagrafics fath = new Anagrafics("Roberto");

fath.setIdentifier("father");
you.setIdentifier("you");
you.setAddress(new Address("Milan"));

//simple hashmap key value
HashMap&#60;String, String&#62; <b>maps</b>;
maps.put("cioppy","bau");</code></pre>
<pre><i>(wait - Just to show to you how Game.class is defined in my mind)
class Game extends HckReflect{
  String name;
  setName(String name){this.name=name};
  getName(){return name};
  getFancyName(){return "+*+*"+name+"*+*+";}
}</i>
(ok, you can continue now)</pre>
<pre>
<code>
Game favGame = new Game();
g.setName("undertale");
me.setFavouriteGame(favGame);

//building the "list_bestWords" mapping object

Words w1 = new Words("bau","me");
Words w2 = new Words("cioppy","barik");
Words w3 = new Words("cioppi","me");

//i am setting the desired personalized identifier to the first element of my list (the one that will be checked by the engine)
w1.setIdentifier("bestWords");

List&#60;HckReflect&#62; bestWordsList = Lists.newArrayList();
bestWordsList.add(w1);
bestWordsList.add(w2);
bestWordsList.add(w3);


//and here the same childrens of the previous example...
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

//now list of single objects
List&#60;HckReflect&#62; <b>myListOfObj</b> = Lists.newArrayList();
myListOfObj.add(me);
myListOfObj.add(you);
myListOfObj.add();

//and here preparing my list of *list of objects*
List&#60;HckReflect&#62; <b>myListObjectsList</b> = Lists.newArrayList();
myListObjectsList.add(yourChilds);
myListObjectsList.add(bestWordsList);

docxService.generateDocument(template.docx, outputFile,<b>myListOfObj</b>, <b>myListObjectsList</b> ,<b>maps</b>)

</code></pre>
<pre><code><b><u>out.docx </u></b>
Today is 20/03/2019

In my hashmap cioppy stay for bau

Your city is Milan

I think that +*+*Undertale*+*+ is one of the best game evvah!

Theese are the best words of the world:
bau---me
cioppy---barik
cioppi---me

Theese are the childrens of Roberto:

Giorgio, Lisa-asiL, 10
Mario, Unknow-Unknow, 12
Pippo, The crazy one-Many many crazy, 13
</code></pre>

<h1>POM.XML or others package managers at:</h1>

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
<br/><br/>
Please write to me <hck@hackubau.it> if you have any questions.

