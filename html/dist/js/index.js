
    function openTab(evt, tabName) {
      var i, tabcontent, tablinks, subtablinks;
      tabcontent = document.getElementsByClassName("tabcontent");
      for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
      }
      tablinks = document.getElementsByClassName("tablinks");
      for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
      }

      subtablinks = document.getElementsByClassName("subtablinks");
      for (i = 0; i < subtablinks.length; i++) {
        subtablinks[i].className = subtablinks[i].className.replace(" active", "");
      }


      document.getElementById(tabName).style.display = "block";
      evt.currentTarget.className += " active";

      if(tabName == "Create")
        document.getElementById("defaultSubOpen").click();

    }


    function openSubTab(evt, tabName) {
      var i, tabcontent, tablinks;
      tabcontent = document.getElementsByClassName("subtabcontent");
      for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
      }
      tablinks = document.getElementsByClassName("subtablinks");
      for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
      }
      document.getElementById(tabName).style.display = "block";
      evt.currentTarget.className += " active";
    }

    function openQuantityTab(evt, tabName) {
      var i, tabcontent, tablinks;
      tabcontent = document.getElementsByClassName("quantitytabcontent");
      for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
      }
      tablinks = document.getElementsByClassName("quantitytablinks");
      for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
      }
      document.getElementById(tabName).style.display = "block";
      evt.currentTarget.className += " active";
    }


    // Get the element with id="defaultOpen" and click on it
    document.getElementById("defaultOpen").click();


    // Get the modal
    var modalEditProduct = document.getElementById("edit_product_modal");

    // Get the button that opens the modal
    var btnEditProduct = document.getElementById("edit_product_button");

    // Get the <span> element that closes the modal
    var spanEditProduct = document.getElementById("close_edit_product");

    // When the user clicks the button, open the modal 
    btnEditProduct.onclick = function() {
      modalEditProduct.style.display = "block";
    }

    // When the user clicks on <span> (x), close the modal
    spanEditProduct.onclick = function() {
      modalEditProduct.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modal) {
        modalEditProduct.style.display = "none";
      }
    }


    // Get the modal
    var modalEditGroup = document.getElementById("edit_group_modal");

    // Get the button that opens the modal
    var btnEditGroup = document.getElementById("edit_group_button");

    // Get the <span> element that closes the modal
    var spanEditGroup = document.getElementById("close_edit_group");

    // When the user clicks the button, open the modal 
    btnEditGroup.onclick = function() {
      modalEditGroup.style.display = "block";
    }

    // When the user clicks on <span> (x), close the modal
    spanEditGroup.onclick = function() {
      modalEditGroup.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modal) {
        modalEditGroup.style.display = "none";
      }
    }

    // Get the modal
    var modalDeleteProduct = document.getElementById("delete_product_modal");

    // Get the button that opens the modal
    var btnDeleteProduct = document.getElementById("delete_product_button");

    // Get the <span> element that closes the modal
    var spanDeleteProduct = document.getElementById("close_delete_product");

    var submitProductButton = document.getElementById("delete_product_button_submit");

    var cancelProductButton = document.getElementById("delete_product_button_cancel");

    // When the user clicks the button, open the modal 
    btnDeleteProduct.onclick = function() {
      modalDeleteProduct.style.display = "block";
    }

    // When the user clicks on <span> (x), close the modal
    spanDeleteProduct.onclick = function() {
      modalDeleteProduct.style.display = "none";
    }

    submitProductButton.onclick = function() {
      modalDeleteProduct.style.display = "none";
    }

    cancelProductButton.onclick = function() {
      modalDeleteProduct.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modal) {
        modalDeleteProduct.style.display = "none";
      }
    }

    // Get the modal
    var modalDeleteGroup = document.getElementById("delete_group_modal");

    // Get the button that opens the modal
    var btnDeleteGroup = document.getElementById("delete_group_button");

    // Get the <span> element that closes the modal
    var spanDeleteGroup = document.getElementById("close_delete_group");

    var submitButtonGroup = document.getElementById("delete_group_button_submit");

    var cancelButtonGroup = document.getElementById("delete_group_button_cancel");

    // When the user clicks the button, open the modal 
    btnDeleteGroup.onclick = function() {
      modalDeleteGroup.style.display = "block";
    }

    // When the user clicks on <span> (x), close the modal
    spanDeleteGroup.onclick = function() {
      modalDeleteGroup.style.display = "none";
    }

    submitButtonGroup.onclick = function() {
      modalDeleteGroup.style.display = "none";
    }

    cancelButtonGroup.onclick = function() {
      modalDeleteGroup.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modal) {
        modalDeleteGroup.style.display = "none";
      }
    }

    // Get the modal
    var modalChangeQuantity = document.getElementById("change_quantity_modal");

    // Get the button that opens the modal
    var btnChangeQuantity = document.getElementById("change_quantity_button");

    // Get the <span> element that closes the modal
    var spanChangeQuantity = document.getElementById("close_change_quantity");

    // When the user clicks the button, open the modal 
    btnChangeQuantity.onclick = function() {
      modalChangeQuantity.style.display = "block";

      document.getElementById("defaultQuantityOpen").click();

    }

    // When the user clicks on <span> (x), close the modal
    spanChangeQuantity.onclick = function() {
      modalChangeQuantity.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modal) {
        modalChangeQuantity.style.display = "none";
      }
    }

      $(document).ready(function(){
        // console.log("hi");
        getAllProducts();
        // getAllGroups();
      });

      function getAllProducts(){

        $.ajax({
          url: 'http://localhost:8001/api/good ',

          type: 'GET',
          // data: send_data,
          // dataType:'json',
          success: function (json) {
                
                      var d = document.getElementById('Products'); 
                      
                      d.innerHTML = '';
                      json.forEach(function(item){

                    var product=formProduct(item);
                    d.innerHTML+=product;

                  });

                
                },
                error: function (json) {
                    alert(json.errors);


                }

          
        });
      }

      function formProduct(item){

        var text = '<tr id = "'+item.id+'">';
          text+='     <td>'+item.name+'</td>';
          text+='     <td>'+item.description+'</td>';
          text+='     <td>'+item.price+'</td>';
          text+='     <td>';
          text+='       <div class="column">'+item.quantity+'</div>';
            text+='     <div class="column">';
          text+='         <button class="table_button" id="change_quantity_button">Change</button>';
                    
            text+='     </div>';
          text+='   </td>';
          text+='     <td>'+item.groups+'</td>';
          text+='     <td><button class="table_button" id="edit_product_button">Edit</button></td>';
          text+='     <td><button class="table_button" id="delete_product_button">Delete</button></td>';

          text+='   </tr>';

          return text;

      }












var token;



$(document).on('click','.login_button',function(e){

//login

        var send_data={"name":document.getElementById("login_name").value, 
                        "password":document.getElementById("login_password").value};

        $.ajax({
         url: 'http://localhost:8001/login',  

        method: 'GET',
        dataType: 'json',
        data: send_data,
        success: function(tokenReturned){  

          token = tokenReturned;

        },

        error: function (json) {
              alert(json.errors);
            
        },

      });

      });

             


