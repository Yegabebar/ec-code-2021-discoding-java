$(document).ready(function() {

    setInterval("refreshMessages();", 5000)

});
const contentInput = document.getElementById("content");

contentInput.addEventListener("keyup", function (event) {
    if (event.code === 13) {
        event.preventDefault();
        document.getElementById("sendMessage").click();
    }
});

function refreshMessages(){
    url = window.location.pathname
    var id = url.substring(url.lastIndexOf('/') + 1);

    $.ajax({
      type: "GET",
      url: `/conversations/${id}`,
      dataType: "text",
      success: function(result){
        dom = stringToHTML(result);
        console.log("DOM: ",dom)
        messages = dom.children[0].children[0].children[1].children[1].children[0].children[0].children[1].children[1].children[0]
        for(item of messages['children']){
            // Condition if message not already in DOM
            console.log(item)

            //document.getElementById("messages")[0].innerHTML += item.outerHTML

        }
        document.getElementById("messages").innerHTML = messages.outerHTML
      }
  });

}
  var stringToHTML = function (str) {
  	var parser = new DOMParser();
  	var doc = parser.parseFromString(str, 'text/html');
  	return doc.body;
  };