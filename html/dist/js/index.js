$(document).ready(function(){




    getAllProducts();
    getAllGroups();

    document.getElementById("defaultOpen").click();


    var myToken = localStorage["tokenItem"] ;//localStorage.getItem("tokenItem");


    var current_id = 0;


    // Get the modal
    var modalEditProduct = document.getElementById("edit_product_modal");

    // Get the <span> element that closes the modal
    var spanEditProduct = document.getElementById("close_edit_product");

    $(document).on("click", "#edit_product_button", function(e){
      modalEditProduct.style.display = "block";

      var elements = e.target.parentElement.parentElement;

      document.getElementById('edit_product_name').value = elements.childNodes[1].innerHTML;
      document.getElementById('edit_product_description').value = elements.childNodes[3].innerHTML;
      document.getElementById('edit_product_price').value = elements.childNodes[5].innerHTML;
      document.getElementById('edit_product_producer').value = elements.childNodes[8].innerHTML;
      document.getElementById('edit_product_groups').value = elements.childNodes[10].innerHTML;

      current_id = e.target.parentElement.parentElement.id;
      current_id = current_id.substring(0,current_id.length-8);

      
    });

 $(document).on("click", "#edit_product_button_submit", function(e){
      modalEditProduct.style.display = "none";

      var name = document.getElementById('edit_product_name').value;
      var description = document.getElementById('edit_product_description').value;
      var price = document.getElementById('edit_product_price').value;
      var producer = document.getElementById('edit_product_producer').value;
      var groups = document.getElementById('edit_product_groups').value;

      var quantity = document.getElementById(current_id+'_product').childNodes[7].childNodes[1].innerHTML;

      updateProduct(current_id, name, description, quantity, price, producer, groups);

      current_id = 0;

      
    });


    // When the user clicks on <span> (x), close the modal
    spanEditProduct.onclick = function() {
      modalEditProduct.style.display = "none";
      current_id = 0;

    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modalEditProduct) {
        modalEditProduct.style.display = "none";
        current_id = 0;

      }
    }


    // Get the modal
    var modalEditGroup = document.getElementById("edit_group_modal");

    // Get the <span> element that closes the modal
    var spanEditGroup = document.getElementById("close_edit_group");


    $(document).on("click", "#edit_group_button", function(e){
      modalEditGroup.style.display = "block";

      var elements = e.target.parentElement.parentElement;

      document.getElementById('edit_group_name').value = elements.childNodes[1].innerHTML;
      document.getElementById('edit_group_description').value = elements.childNodes[3].innerHTML;

      current_id = e.target.parentElement.parentElement.id;
      current_id = current_id.substring(0,current_id.length-6);

      
    });

 $(document).on("click", "#edit_group_button_submit", function(e){
      modalEditGroup.style.display = "none";

      var name = document.getElementById('edit_group_name').value;
      var description = document.getElementById('edit_group_description').value;

      updateGroup(current_id, name, description);

      current_id = 0;

      
    });
    // When the user clicks on <span> (x), close the modal
    spanEditGroup.onclick = function() {
      modalEditGroup.style.display = "none";

      current_id = 0;
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modalEditGroup) {
        modalEditGroup.style.display = "none";
        current_id = 0;
      }
    }



    // Get the modal
    var modalDeleteProduct = document.getElementById("delete_product_modal");

    // Get the <span> element that closes the modal
    var spanDeleteProduct = document.getElementById("close_delete_product");

    // When the user clicks the button, open the modal 
    $(document).on("click", "#delete_product_button", function(e){

      modalDeleteProduct.style.display = "block";

      current_id = e.target.parentElement.parentElement.id;
      current_id = current_id.substring(0,current_id.length-8);

    });

    // When the user clicks on <span> (x), close the modal
    spanDeleteProduct.onclick = function() {
      modalDeleteProduct.style.display = "none";
      current_id = 0;
    }

    $(document).on("click", "#delete_product_button_submit", function(){



      deleteProduct(current_id);

      current_id = 0;
      


    });

    $(document).on("click", "#delete_product_button_cancel", function(){

      modalDeleteProduct.style.display = "none";
      current_id = 0;
    });

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modalDeleteProduct) {
        modalDeleteProduct.style.display = "none";
      }
    }

    // Get the modal
    var modalDeleteGroup = document.getElementById("delete_group_modal");

    // Get the <span> element that closes the modal
    var spanDeleteGroup = document.getElementById("close_delete_group");

    // When the user clicks the button, open the modal 
    $(document).on("click", "#delete_group_button", function(e){

      modalDeleteGroup.style.display = "block";

      current_id = e.target.parentElement.parentElement.id;
      current_id = current_id.substring(0,current_id.length-6);
    });

    // When the user clicks on <span> (x), close the modal
    spanDeleteGroup.onclick = function() {
      modalDeleteGroup.style.display = "none";
      current_id = 0;

    }

    $(document).on("click", "#delete_group_button_submit", function(){


      deleteGroup(current_id);
      current_id = 0;


    });

    $(document).on("click", "#delete_group_button_cancel", function(){

      modalDeleteGroup.style.display = "none";
      current_id = 0;

    });

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modalDeleteGroup) {
        modalDeleteGroup.style.display = "none";
      }
    }

    // Get the modal
    var modalChangeQuantity = document.getElementById("change_quantity_modal");

    // Get the <span> element that closes the modal
    var spanChangeQuantity = document.getElementById("close_change_quantity");

    // When the user clicks the button, open the modal 
    $(document).on("click", "#change_quantity_button", function(e){

      modalChangeQuantity.style.display = "block";

      var name = e.target.parentElement.parentElement.parentElement.childNodes[1].innerHTML;

      modalChangeQuantity.childNodes[3].childNodes[1].childNodes[3].innerHTML='Add or write off '+name;

      current_id = e.target.parentElement.parentElement.parentElement.id;

      current_id = current_id.substring(0,current_id.length-8);

      document.getElementById("defaultQuantityOpen").click();

    });

    $(document).on("click", "#add_good_button", function(){


      var num = document.getElementById("input_product_quantity_add").value;

      increment(current_id, num);
      current_id = 0;
      document.getElementById("input_product_quantity_add").value='';


    });


     $(document).on("click", "#write_off_good_button", function(){


      var num = document.getElementById("input_product_quantity_write_off").value;

      decrement(current_id, num);
      current_id = 0;
      document.getElementById("input_product_quantity_write_off").value='';


    });

    // When the user clicks on <span> (x), close the modal
    spanChangeQuantity.onclick = function() {
      modalChangeQuantity.style.display = "none";
      current_id = 0;

    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
      if (event.target == modalChangeQuantity) {
        modalChangeQuantity.style.display = "none";
      current_id = 0;

      }
    }



    $(document).on("click", "#create_product_button", function(e){

        var name = document.getElementById('input_product_name').value;
        var description = document.getElementById('input_product_description').value;
        var price = document.getElementById('input_product_price').value;
        var producer = document.getElementById('input_product_producer').value;
        var groups = document.getElementById('input_product_groups').value;

        document.getElementById('input_product_name').value='';
        document.getElementById('input_product_description').value='';
        document.getElementById('input_product_price').value='';
        document.getElementById('input_product_producer').value='';
        document.getElementById('input_product_groups').value='';

        createProduct(name, description, "0", price, producer, groups);


    });

    $(document).on("click", "#create_group_button", function(e){

        var name = document.getElementById('input_group_name').value;
        var description = document.getElementById('input_group_description').value;
        
        document.getElementById('input_group_name').value='';
        document.getElementById('input_group_description').value='';

        createGroup(name, description);


    });


    $(document).on("click", "#search_product_button", function(e){

        var name = document.getElementById('search_product_name').value;
        var description = document.getElementById('search_product_description').value;
        var quantityMore = document.getElementById('search_product_quantity_more').value;
        var quantityLess = document.getElementById('search_product_quantity_less').value;
        var priceMore = document.getElementById('search_product_price_more').value;
        var priceLess = document.getElementById('search_product_price_less').value;
        var producer = document.getElementById('search_product_producer').value;
        var groups = document.getElementById('search_product_groups').value;

        // document.getElementById('input_product_name').value='';
        // document.getElementById('input_product_description').value='';
        // document.getElementById('input_product_price').value='';
        // document.getElementById('input_product_producer').value='';
        // document.getElementById('input_product_groups').value='';

        searchProduct(name, description, quantityMore, quantityLess, priceMore, priceLess, producer, groups);


    });


    $(document).on("click", "#search_group_button", function(e){

        var name = document.getElementById('search_group_name').value;
        var description = document.getElementById('search_group_description').value;
        
        // document.getElementById('input_group_name').value='';
        // document.getElementById('input_group_description').value='';

        searchGroup(name, description);

    });



      function getAllProducts(){
        $.ajax({
          url: 'http://localhost:8001/api/good',

          type: 'GET',
          headers: {"token": localStorage["tokenItem"] },
          success: function (json) {
                      
                      var d = document.getElementById('Products'); 
                      
                      var text;
                      text = '<table  style="margin-top: 0px;"><tr>';

                      text+='<th>Name</th>';
                      text+='<th>Description</th>';
                      text+='<th>Price</th>';
                      text+='<th>Quantity</th>';
                      text+='<th>Producer</th>'
                      text+='<th>Group</th>';
                      text+='<th>Edit</th>';
                      text+='<th>Delete</th>';

                      text+='</tr>';

                      json = JSON.parse(json);

                      var total = 0;

                      
                      for(var item in json){

                        var product=formProduct(json[item]);
                        total+=json[item].price*json[item].num;
                        text+=product;
                      }

                text+='</table>';

                d.innerHTML=text;

                total = "Total: $" + total;

                document.getElementById("total_price").innerHTML = total;
                
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // alert("ERR   a"+myToken);

                }

          
        });
      }



      function getAllGroups(){

        $.ajax({
          url: 'http://localhost:8001/api/group',

          type: 'GET',
          headers: {"token": localStorage["tokenItem"] },


          success: function (json) {
                
                      var d = document.getElementById('Groups'); 
                      
                      d.innerHTML = '';

                      // console.log(json);


                      var text = '<table  style="margin-top: 0px;">';
                      text += '<tr>';
                      text += '<th>Name</th>';
                       text += '<th>Description</th>';
                       text += '<th>Edit</th>';
                       text += '<th>Delete</th>';
                     text += '</tr>';



                     json = JSON.parse(json);

                      
                          for(var item in json){

                      

                            var group=formGroup(json[item]);
                            text+=group;

                  }

                  text+='</table>';

                d.innerHTML=text;

                
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // alert("ERR   a"+myToken);

                }
          
        });
      }



      function formProduct(item){

        var groupsString = item.groups.join(', ');

        var text = '<tr id = "'+item.id+'_product">';
          text+='     <td>'+item.name+'</td>';
          text+='     <td>'+item.description+'</td>';
          text+='     <td>'+item.price+'</td>';
          text+='     <td>';
          text+='       <div class="column">'+item.num+'</div>';
            text+='     <div class="column">';
          text+='         <button class="table_button" id="change_quantity_button">Change</button>';
                    
            text+='     </div>';
          text+='   </td>';
          text+='<td>'+item.producer+'</td>';
          text+='     <td>'+groupsString+'</td>';
          text+='     <td><button class="table_button" id="edit_product_button">Edit</button></td>';
          text+='     <td><button class="table_button" id="delete_product_button">Delete</button></td>';

          text+='   </tr>';

          return text;

      }


      function formGroup(item){

        var text =  '<tr id = "'+item.id+'_group">';
            text+='    <td>'+item.name+'</td>';
            text+='     <td>'+item.description+'</td>';
            text+='     <td><button class="table_button" id="edit_group_button">Edit</button></td>';
            text+='     <td><button class="table_button" id="delete_group_button">Delete</button></td>';
            text+='   </tr>';

            return text;


      }

      function createProduct(name, description, quantity, price, producer, groups){
        var groupsArr = groups.split(', ');
        
        var send_data = {'name':name, 'description':description, 'num':quantity, 'price':price, 'producer':producer, 'groups':groupsArr};

        // console.log(send_data);
        $.ajax({
          url: 'http://localhost:8001/api/good' ,

          type: 'PUT',
          headers: {"token": localStorage["tokenItem"] },

          data: JSON.stringify(send_data),
          dataType:'text',
          success: function (number) {
                    alert('Created');

                    var id = number;

                    var d = document.getElementById('Products'); 
                      
                      var text = d.innerHTML;
                      text = text.substring(0,d.innerHTML.length-8);

                      send_data['id'] = id;
                      text+=formProduct(send_data);

                      text+='</table>';

                      d.innerHTML = text;




                              
                },
               error: function (jqXHR, textStatus, errorThrown) {
                    // alert("ERR   a"+myToken);
                    alert(jqXHR);
                    alert(textStatus);
                    alert(errorThrown);

                }

        });

      }


      function createGroup(name, description){
        
        var send_data = {'name':name, 'description':description};

        $.ajax({
          url: 'http://localhost:8001/api/group' ,

          type: 'PUT',
          headers: {"token": localStorage["tokenItem"] },

          data: JSON.stringify(send_data),
          dataType:'text',
          success: function (number) {
                    alert('Created');

                    var id = number;

                    var d = document.getElementById('Groups'); 
                      
                      var text = d.innerHTML;
                      text = text.substring(0,d.innerHTML.length-8);

                      send_data['id'] = id;
                      text+=formGroup(send_data);

                      text+='</table>';

                      d.innerHTML = text;




                              
                },
               error: function (jqXHR, textStatus, errorThrown) {
                    // alert("ERR   a"+myToken);
                    alert(jqXHR);
                    alert(textStatus);
                    alert(errorThrown);

                }

        });

      }


      function searchProduct(name, description, quantityMore, quantityLess, priceMore, priceLess, producer, groups){

         $.ajax({
          //todo url and data
          url: 'http://localhost:8001/api/good',

          type: 'GET',
          headers: {"token": localStorage["tokenItem"] },
          success: function (json) {
                      
                      var d = document.getElementById('Products'); 
                      
                      var text;
                      text = '<table  style="margin-top: 0px;"><tr>';

                      text+='<th>Name</th>';
                      text+='<th>Description</th>';
                      text+='<th>Price</th>';
                      text+='<th>Quantity</th>';
                      text+='<th>Producer</th>'
                      text+='<th>Group</th>';
                      text+='<th>Edit</th>';
                      text+='<th>Delete</th>';

                      text+='</tr>';

                      json = JSON.parse(json);

                      var total = 0;

                      
                      for(var item in json){

                        var product=formProduct(json[item]);
                        total+=json[item].price*json[item].num;
                        text+=product;
                      }

                text+='</table>';

                d.innerHTML=text;

                total = "Total: $" + total;

                document.getElementById("total_price").innerHTML = total;


                document.getElementById("defaultOpen").click();

                
                },
                error: function (jqXHR, textStatus, errorThrown) {
                  
                    // alert("ERR   a"+myToken);

                }

          
        });
      }


      function searchGroup(name, description){


        var send_data = {'name':name, 'description':description};

        $.ajax({

          //todo url and data
          url: 'http://localhost:8001/api/group' ,

          type: 'PUT',
          headers: {"token": localStorage["tokenItem"] },

          data: JSON.stringify(send_data),
          dataType:'text',
          success: function (json) {
                    


                    var d = document.getElementById('Groups'); 
                      
                      d.innerHTML = '';

                      // console.log(json);


                      var text = '<table  style="margin-top: 0px;">';
                      text += '<tr>';
                      text += '<th>Name</th>';
                       text += '<th>Description</th>';
                       text += '<th>Edit</th>';
                       text += '<th>Delete</th>';
                     text += '</tr>';



                     json = JSON.parse(json);

                      
                          for(var item in json){

                      

                            var group=formGroup(json[item]);
                            text+=group;

                  }

                  text+='</table>';

                d.innerHTML=text;



                document.getElementById("groupsOpen").click();





                              
                },
               error: function (jqXHR, textStatus, errorThrown) {
                    // alert("ERR   a"+myToken);
                    alert(jqXHR);
                    alert(textStatus);
                    alert(errorThrown);

                }

        });

      }



      function deleteProduct(id){


        $.ajax({
          url: 'http://localhost:8001/api/good/'+id ,

          headers: {"token": localStorage["tokenItem"] },

          type: 'DELETE',
          // data: send_data,
          // dataType:'json',
          success: function () {
                    // alert('Deleted');
                     modalDeleteProduct.style.display = "none";


                    var element = document.getElementById(id+'_product');
                    element.parentNode.removeChild(element);


                              
                },
                error: function (json) {
                    alert(json.errors);


                }

        });

      }


      function deleteGroup(id){


        $.ajax({
          url: 'http://localhost:8001/api/group/'+id ,

          headers: {"token": localStorage["tokenItem"] },
          
          type: 'DELETE',
          // data: send_data,
          // dataType:'json',
          success: function () {
                    // alert('Deleted');
                    modalDeleteGroup.style.display = "none";


                    var element = document.getElementById(id+'_group');
                    element.parentNode.removeChild(element);

                    //todo
                    getAllProducts();


                              
                },
                error: function (json) {
                    alert(json.errors);


                }

        });

      }


       function increment(id, num){

        var send_data = {'id':id, 'num':num};

        $.ajax({
          url: 'http://localhost:8001/api/good/increment'  ,

          type: 'POST',
          headers: {"token": localStorage["tokenItem"] },
          
          data: JSON.stringify(send_data),
          dataType:'text',
         
          success: function () {
                    // alert('Incremented');
                    modalChangeQuantity.style.display = "none";

                    
                    var quantity = document.getElementById(id+'_product').childNodes[7].childNodes[1].innerHTML;
                  
                    var num2 =parseInt(num) + parseInt(quantity);

                    document.getElementById(id+'_product').childNodes[7].childNodes[1].innerHTML = num2;
                              
                },
                error: function (json) {
                  console.log("a");
                    alert(json.errors);


                }

          
        });

      }


         function decrement(id, num){

        var send_data = {'id':id, 'num':num};

        $.ajax({
          url: 'http://localhost:8001/api/good/decrement'  ,

          type: 'POST',
          headers: {"token": localStorage["tokenItem"] },
          
          data: JSON.stringify(send_data),
          dataType:'text',
         
          success: function () {
                    // alert('Incremented');
                    modalChangeQuantity.style.display = "none";

                    
                    var quantity = document.getElementById(id+'_product').childNodes[7].childNodes[1].innerHTML;
                  
                    var num2 =parseInt(quantity) - parseInt(num);

                    document.getElementById(id+'_product').childNodes[7].childNodes[1].innerHTML = num2;
                              
                },
                error: function (json) {
                    alert(json.errors);


                }

          
        });

      }


      function updateProduct(id, name, description, quantity, price, producer, groups){

        var groupsArr = groups.split(', ');


        var send_data = {'id':id, 'name':name, 'description':description, 'num':quantity, 'price':price, 'producer':producer, 'groups':groupsArr};



        $.ajax({
          url: 'http://localhost:8001/api/good'  ,

          type: 'POST',
          headers: {"token": localStorage["tokenItem"] },
          
          data: JSON.stringify(send_data),
          dataType:'text',
         
          success: function () {
                    // alert('success');
                    modalEditProduct.style.display = "none";

                    var elements = document.getElementById(id+'_product');

                    elements.childNodes[1].innerHTML = name;
                    elements.childNodes[3].innerHTML = description;
                    elements.childNodes[5].innerHTML = price;
                    elements.childNodes[8].innerHTML = producer;
                    elements.childNodes[10].innerHTML = groups;
                              
                },
                error: function (json) {
                    alert(json.errors);


                }

          
        });
      }



        function updateGroup(id, name, description){


        var send_data = {'id':id, 'name':name, 'description':description};



        $.ajax({
          url: 'http://localhost:8001/api/group'  ,

          type: 'POST',
          headers: {"token": localStorage["tokenItem"] },
          
          data: JSON.stringify(send_data),
          dataType:'text',
         
          success: function () {
                    alert('success');
                    modalEditGroup.style.display = "none";

                    var elements = document.getElementById(id+'_group');

                    elements.childNodes[1].innerHTML = name;
                    elements.childNodes[3].innerHTML = description;

                    getAllProducts();
                              
                },
                error: function (json) {
                    alert(json.errors);


                }

          
        });
      }




});
