/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


// A $( document ).ready() block.
/*
 $(document).ready(function () {
 console.log("ready!");
 $("#refreshParking").click();
 
 $("#refreshParking").click(function () {
 console.log("click!")
 });
 document.getElementById('r12').click();
 });*/



function closeMessage() {
     console.log("click");
    setTimeout(function () {
        $('.message').fadeOut(1000,'swing');
        console.log("close");
    }, 5000);
}
