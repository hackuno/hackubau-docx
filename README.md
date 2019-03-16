# Hackubau Word Generator by template.docx
<span class="lead"><b><maven: hackubau-docx></b> Mircrosoft Word (.docx) & OpenOffice (.docx) compatibility</span>


<h1><b>What is this?</b></h1>
This is a Service to perform susbstitution of placeholders in .docx files (templates) writing simply something like this in the word template:
<br>
<code> ${yourObject.yourField} </code>
<br>
<code> ${yourObject.yourField.yourEventuallyNestedField} </code>
<br>
<code> ${list_yourObject.yourField1@separator#yourField2.yourEventuallyNestedField@separator#yourfield3} </code>
<h5>Where:</h5
  <p>yourObject= your Object extends HckReflect className or the specified HckReflect.identifier property for every passed object</p>
  <p>yourField= name of the getMethod. example, to invoke getName() of Speciality.class you will write "${speciality.name}</p>

<br><b>You can easly pass your custom Object/List of objects and the engine will retrieve everything!</b>


<h1 class="lead">Case of use</h1>

<h4 class="lead">Do you have to create Word(.docx) files starting by a template and substituting some placeholders with right values?
This tool will make it so easy that you will be stunned! </h4>

<h1 class="lead">Hey, but i need an ad easy way to retrieve my Objects properties and place them in my .docx files without write a line of code!</h1>

<h4 class="lead">You will have just to make your objects extending my HckReflect.class and pass them all to my service!</h4>
<h5 class="lead">The remaining is just about .docx template! Demand it to your customers.</h5>

