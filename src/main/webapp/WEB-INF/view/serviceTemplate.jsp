<%@ page contentType="text/html; charset=utf-8" language="java" errorPage=""%>
<!doctype html>
<html lang="en">
    
<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/bootstrap-glyphicons.css" rel="stylesheet">    
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>MOS Proxy Simulator</title>
</head>

<body>

<center>
<p> MOS Proxy Simulator </p>
	<form id="sendReq" method="post" >
		<font face="Verdana,sans-serif;" size="2">
			<table cellspacing="20">
						
			<tr>
			<table border="1" >	
			 <tr>
                    <td align="right">Select HTTP Method: </td>
					<td align="left">
			             <select size="1" name="servicMethod" >						 
						    <option value="Select" selected="selected">Select</option> 
                            <option value="POST" >POST</option>
                            <option value="GET" >GET</option>
                          </select>
                    </td>
				  </tr>  
			
			     <tr>
                    <td align="right">Service URL to execute: </td>
					<td align="left">
						<input type="text" name="servicURL"  value="http://localhost:6294/maxim/o2mosservlet" size="100"  />
				    </td>
				  </tr>                 
			</table>
			</tr> 
			
			<tr>
			<table border="1" >	
			     <tr>
                    <td align="right">Request body Data : </td>
					<td align="left">
			          <textarea rows="8" cols="100"  name="requestBody" >
                         ?USER=O55app&Password=o2fm!&TP_XML=%3c%3fxml+version%3d%221.0%22+encoding%3d%22utf-8%22+standalone%3d%22no%22%3f%3e%3c!DOCTYPE+WIN_TPBOUND_MESSAGES+SYSTEM+%22tpbound_messages_v1.dtd%22%3e%3cWIN_TPBOUND_MESSAGES%3e%3cSMSTOTP%3e%3cSOURCE_ADDR%3e%2b447778045512%3c%2fSOURCE_ADDR%3e%3cTEXT%3eManonsite%3c%2fTEXT%3e%3cWINTRANSACTIONID%3e488146111%3c%2fWINTRANSACTIONID%3e%3cDESTINATION_ADDR%3e62946%3c%2fDESTINATION_ADDR%3e%3cSERVICEID%3e3%3c%2fSERVICEID%3e%3cNETWORKID%3e1%3c%2fNETWORKID%3e%3cARRIVALDATETIME%3e%3cDD%3e22%3c%2fDD%3e%3cMMM%3eJUN%3c%2fMMM%3e%3cYYYY%3e2008%3c%2fYYYY%3e%3cHH%3e10%3c%2fHH%3e%3cMM%3e40%3c%2fMM%3e%3c%2fARRIVALDATETIME%3e%3c%2fSMSTOTP%3e%3c%2fWIN_TPBOUND_MESSAGES%3e
			          </textarea>   					 
                    </td>
				  </tr>  				
			</table>
			</tr> 
	</table>					
		    <table border="1" >			
				<tr>
				</tr>
				<tr>
					<td align="right">
						<input type="reset" name="reset" value="Clear Fields" />
					</td>
					<td align="left">
						<input type="submit" name="send" id="apisubmitButton" value="Send Request"/>
					</td>
				</tr>				
			</table>
			
		</font>
	</form>
    
   <div id="exResult" class="row">
            <div class="col-12">
            <div class="card" >
                                <div class="card-header"> API Response Data Pane </div>
                                <div class="card-body">
                                       <div class="row" >
                                           <div style="height:250px; width:100%;overflow: scroll" id="exResultDiv">
                                           </div>

                                       </div>
                                </div>
                </div>
            
            </div>  
        </div> 
</center>
    
    <script src="/javaScripts/jquery-3.2.1.min.js"></script>
    <script src="/javaScripts/bootstrap.min.js"></script>
    <script src="/javaScripts/json.js"></script>         
    
       <script type="text/javascript">
        $(document).ready(function()
        {        
            $("form#sendReq").submit(function(event){
                  var isallOk  = valiadteRequest();
                  if(isallOk){
                          event.preventDefault();
                          var formData = new FormData($(this)[0]);

                          $.ajax({
                            url: '/maxim/executeMosProxySimulator.cms',
                            type: 'POST',
                            data: formData,
                            async: false,
                            cache: false,
                            contentType: false,
                            processData: false,
                            success: function (returndata) {
                              $("#apisubmitButton").prop( "disabled", true );    
                              UpdateMessageArea("Request Submitted Suvessfully" + returndata.status);
                              UpdateMessageArea("Response Data recived " + returndata.Message);       
                              },
                            error:function (returndata) {
                              UpdateMessageArea("Problem processing Request" + returndata.status);
                              UpdateMessageArea("Error Message recived " + returndata.Message);          
                              }
                          });
                  }
                  return false;
            });
            
            
        });     
        
            function valiadteRequest()
            {	
                var  currentForm = document.forms[0]
                var  selectedItemIndex = currentForm["servicMethod"].selectedIndex;	
                if(selectedItemIndex == 0){
                  alert(" please select valid HTTP method for service being called ") ;
                  return false;	  
                 }
                 return true;
            }
           
               function UpdateMessageArea(message){
                    $( "#exResultDiv" ).append("<br>" +message);
                    var myDiv = document.getElementById('exResultDiv');
                    myDiv.scrollTop = myDiv.scrollHeight;
                }
</script>
    
</body>	
</html>

