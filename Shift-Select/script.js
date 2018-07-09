var items=[...(document.getElementsByClassName('check'))];  //selecting the checkboxe's item and converting them to an array
let lastItem;    //lastcheckeditem to start the shift-checked array from 

//actionlistener
for(element of items){
    element.addEventListener('click',handleShift)
}

/**
 * 
 * 1) checks if the shift key is held and a selection is happening at the same time
 * 2) forms a subarray between the lastcheckeditem and the current checked one
 * 3) checks all the items of this array
 * 4) keeps track of the current checked item 
 * 
 */
function handleShift(e){
if(e.shiftKey&&this.checked){

    //Math max and min is used so that the feature would work from bottom to top as well
    var itemsToCheck=items.slice(Math.min(items.indexOf(this),items.indexOf(lastItem)),Math.max(items.indexOf(this),items.indexOf(lastItem)));

    for(item of itemsToCheck){
        item.checked=true;
    }

}  
  if(this.checked)
  lastItem=this;
}