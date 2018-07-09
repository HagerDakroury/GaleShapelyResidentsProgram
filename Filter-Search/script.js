//selecting

var sInput = document.getElementById('searchInput');

var aInput = document.getElementById('addInput');
var aBtn = document.getElementById('addBtn');

var theList = document.getElementById('items');

console.log(theList.children.length);

//adding eventListeners
aBtn.addEventListener('click', addClicked);
sInput.addEventListener('keyup', searchChoosen)
sInput.addEventListener('keydown', searchChoosen)


//action Functions
function searchChoosen(event) {
  
        for (var i = 0; i < theList.children.length; i++) {
            if (theList.children[i].textContent.indexOf(sInput.value) == -1)
                theList.children[i].style.display = 'none';
            else 
                theList.children[i].style.display = 'block';
        }

   
}

function addClicked(event) {
    if (aInput.value != "") {
        theList.innerHTML += `<li class="list-group-item text-center">${aInput.value}</li>`;
    }

}