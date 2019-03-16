# Hackubau Word Generator by template.docx - maven: hackubau-docx
<h5><b> Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility</b></h5><br>
# What is
It is a Service to perform susbstitution of placeholders in .docx files (templates) writing simply something like this in the word template:
<br><br>
<code> ${yourObject.yourField} </code>
<p>Where yourObject= your Object extends HckReflect className or the specified HckReflect.identifier property for every passed object</p>
<p>Where yourField= name of the getMethod. example, to invoke getName() of Speciality.class you will write "${speciality.name}</p>
<br>
<code> ${yourObject.yourField.yourEventuallyNestedField} </code>
<br>
<code> ${list_yourObject.yourField1@separator#yourField2.yourEventuallyNestedField@separator#yourfield3} </code>
<br>
<br>You can pass easly your custom Object/List of objects and the engine will retrieve everything!


<h1 class="lead">Case of use</h1>

<h4 class="lead">Do you have to create Word(.docx) files starting by a template and substituting some placeholders with right values?
This tool will make it so easy that you will be stunned! </h4>

<h1 class="lead">Hey, but i need an ad easy way to retrieve my Objects properties and place them in my .docx files without write a line of code!</h1>

<h4 class="lead">You will have just to make your objects extending my HckReflect.class and pass them all to my service!</h4>
<h5 class="lead">The remaining is just about .docx template! Demand it to your customers.</h5>

