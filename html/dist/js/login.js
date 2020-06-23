$(document).ready(function(){


// var myToken;


  var link = 'file:///D:/Programming/Sklad/html/index.html';


$(document).on('click','.login_button',function(e){

//login

              var login = document.getElementById("login_name").value;

              var password = MD5(document.getElementById("login_password").value);

// alert(login+" "+password);

       // var send_data={"name":document.getElementById("login_name").value, 
         //               "password":document.getElementById("login_password").value};

         // var url1 =  'http://localhost:8001/login?login='+login+'&password='+password;
         // console.log(url1);
        $.ajax({
         url: 'http://localhost:8001/login?login='+login+'&password='+password,  
         method: 'GET',
        success: function(tokenReturned){  

          // myToken = tokenReturned;
          localStorage["tokenItem"] = tokenReturned;

          
          // localStorage.setItem("tokenItem", tokenReturned); 

          // alert(myToken);

          window.location.href = link;
          // window.location.replace(link);

        },
        error: function(xhr, status, error){
                     var errorMessage = xhr.status + ': ' + xhr.statusText
                     alert('Error - ' + errorMessage);
                 }

      });

  });



});
