let inp = document.getElementById("uploadImage");
console.log(inp);
let but = document.getElementById("loadImageSubmitButton");
console.log(but);
inp.addEventListener('change', function () {
    but.click();
});