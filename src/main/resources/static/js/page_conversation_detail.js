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
        messages = dom.children[0].children[0].children[1].children[1].children[0].children[0].children[1].children[1].children[0]
        document.getElementById("messages").innerHTML = messages.innerHTML
      }
  });

}
  var stringToHTML = function (str) {
  	var parser = new DOMParser();
  	var doc = parser.parseFromString(str, 'text/html');
  	return doc.body;
  };