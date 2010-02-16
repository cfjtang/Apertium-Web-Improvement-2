<%-- 
    Document   : register
    Created on : 18-ago-2009, 12:37:25
    Author     : vmsanchez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Apertium Web Service register page</title>
    </head>
    <body>
        <h1>Apertium Web Service register page</h1>

        <h3>Terms and conditions</h3>
        <p>
            Lorem ipsum an tibique mediocritatem duo. Nec te inermis luptatum legendos, probo sensibus erroribus eam ex, pro cu possit omnesque. Nam at melius percipitur assueverit, possit facete no pri. At atomorum vituperata comprehensam has, ex populo aliquid impedit nec. Per an dicit partem. Eu equidem sensibus dignissim est, debet conclusionemque pro no.
<p>
Ei mucius everti ius, has id saepe vivendo dissentias, te vim amet dicant labitur. Cu ius natum magna nemore, id porro constituam cotidieque vis. Cu vix dicant officiis definitionem, eius offendit repudiandae no mea. Timeam docendi tincidunt ut mel, ludus corpora mea id, partiendo incorrupte nam eu. Sumo prima melius sea in, natum mollis constituam ne usu, duo justo quaerendum liberavisse ad. Facilisi pertinax democritum cu mea, duo mutat soluta sensibus ad, ex per posse bonorum facilisi.
</p>

<p>
Eu mollis repudiare contentiones quo, pro ea sint inimicus torquatos, justo congue diceret mei ut. Patrioque honestatis mel ei, id eos quod dico tincidunt. Ius in nonummy iudicabit iracundia. Eum modo clita molestiae an, ei vero dicit ocurreret eos, at scripta aperiam adversarium has. Has te error aliquam accommodare, an iriure maiestatis accommodare vel, eros graeco labitur eos ad. Zzril dicunt impetus ut eum, vim affert eripuit deseruisse et. Eum at qualisque adolescens mediocritatem, mea ne adhuc iudicabit theophrastus, debet laoreet eleifend sed ut.
</p>

<p>
His nisl dignissim te. An qui putant iudicabit efficiantur, pri an inermis eligendi pertinax, doming nominavi reprimique eam ne. Cum an hinc esse disputationi, sed falli novum repudiare te. Eu est copiosae adipiscing efficiantur, noster nostrum invenire te vel. Sea ut meis viderer honestatis, lorem dicant saperet pro ut, te omnis sonet omittantur mel. Omnium ornatus accusamus has ex, sea id diam melius sensibus, et vis numquam labores quaerendum.
</p>

<p>
Pro in ipsum malorum facilis, his choro populo expetendis ex, ea nobis option urbanitas has. Idque scriptorem consequuntur nam no, unum tempor pro no, sit iriure aliquid percipit in. Iudico oporteat liberavisse te nam, quo dicit necessitatibus cu. Offendit omittantur te nam, regione integre platonem ea mel, et qui laudem albucius definitiones. Vel ne noster placerat adipisci, vim cu sint periculis prodesset. Vis omnis utinam putent et, est an sint novum partiendo. An sed paulo nobis eleifend, elitr decore eirmod vel in, nec ei tincidunt omittantur reformidans.
</p>

<p>
Eu sit alia quas, eu mea melius periculis pertinacia. Eum te latine diceret antiopam, nusquam conceptam theophrastus ei usu. Omnes deserunt eu vis, eam eu tota omnium consulatu. Eripuit quaestio imperdiet ad duo, in cum kasd comprehensam, qui duis minimum deseruisse ea. Eum lobortis forensibus scribentur an, vel autem homero feugait ea, ut tamquam qualisque sea.
</p>

<p>
An mei dolor dignissim reformidans, id vim lorem deserunt accommodare, ei doctus delicata ius. No nisl veniam pri, cum esse error te. Ad electram laboramus sea, sonet errem docendi ei mel. Ut sed sententiae mediocritatem, ex mei mediocrem contentiones, an eam doctus vituperata. Vis wisi vivendum probatus id, eu vim autem omittantur. Vel id veniam oportere urbanitas, cum ea vivendo suscipit moderatius.
</p>

<p>
Id aeterno convenire vituperatoribus mei, sea at puto vulputate instructior. In falli vivendo voluptatibus usu, pri vocent aliquid impedit id. Nec ad eirmod torquatos, quas summo probatus sit ex. Mei audiam ponderum prodesset ne, has ei dicat simul luptatum, id debitis inimicus honestatis sea. Ad essent dolorem percipit nam, inani repudiandae mediocritatem at cum, ut fuisset maiestatis sit.
</p>

<p>
Has minimum appetere abhorreant an, an qui mutat dicit nobis, mel laudem omnium moderatius ex. Usu ad altera aeterno takimata, ea aperiri fierent has. Nec eu audiam nominavi definitiones, puto natum blandit his eu, ei posse choro placerat his. Takimata partiendo assentior et eum, duo ea populo qualisque disputationi. Putent omnesque ius te, an per omittam disputando consectetuer, mea mutat facilisis salutatus cu. Eum te blandit prodesset philosophia.
</p>

<p>
Eam ad primis omnesque, corpora conceptam intellegebat ad sed. Mazim veritus urbanitas vim at, no munere putent impedit ius. Nonumy everti denique qui eu, hinc populo doctus ei ius. Duo eleifend scribentur theophrastus an, sed te nullam maiorum ullamcorper, eu nemore euripidis pri. Eleifend explicari dignissim at cum, id sea nostro tamquam consequat, graecis adipisci senserit nec no.
        </p>

        <h3>Register</h3>
        <form action="RegisterUserServlet" method="POST">
            Email: <input type="text" name="email" />
            <br/>
            Website URL: <input type="text" name="url" />
            <br/>
            <input type="checkbox" name="accept"/> I accept the service terms and conditions
            <br/>
            <input type="submit" value="Register">
        </form>
   
        <p>
            <%
            if(request.getAttribute("message")!=null)
                out.println((String)request.getAttribute("message"));
            %>
        </p>
    </body>
</html>
