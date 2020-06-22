$(document).ready(function(){


// var myToken;


  var link = 'file:///D:/univer/summer/%D0%9A%D0%BB%D0%B8%D0%B5%D0%BD%D1%82-%D1%81%D0%B5%D1%80%D0%B2%D0%B5%D1%80/projects/Sklad/html/index.html';


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
        error: function (json) {
              alert(json.errors);
        },

      });

  });



});
