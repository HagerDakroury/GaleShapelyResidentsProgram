//selecting

var sInput=document.getElementById('searchInput');

var aInput=document.getElementById('addInput');
var aBtn=document.getElementById('addBtn');

var theList=document.getElementById('items');



//adding eventListeners
aBtn.addEventListener('click',addClicked);
sInput.addEventListener('keyup',searchChoosen)
sInput.addEventListener('keydown',searchChoosen)


//action Functions
function searchChoosen(event){
if(sInput.value=="")
{
    for(var j=0;j<theList.children.length;j++)
        theList.children[j].style.display='block'; 
}
else
{
  for(var i=0;i<theList.children.length;i++)
    {
        if(theList.children[i].textContent.indexOf(sInput.value)==-1)
            theList.children[i].style.display='none';
    }
       theList.children[i].style.display='block';

}
}

function addClicked(event){
if(aInput.value!="")
{
    theList.innerHTML +=  `<li class="list-group-item text-center">${aInput.value}</li>`;
}

}