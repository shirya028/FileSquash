function showSideBar() {
    document.getElementById("sidebar").style.display="flex";
}

function closeSideBar() {
    document.getElementById("sidebar").style.display="none";
}

const dropArea = document.getElementById("drop-area");
const input_file = document.getElementById("input-file");
const img_view = document.querySelector(".img-view");

input_file.addEventListener("change", () => {
	uploadFile(input_file.files)
	
	}); 

	/*
dropArea.addEventListener("dragover", function(e) {
    e.preventDefault();
});

var temp;
dropArea.addEventListener("drop", function(e) {
    e.preventDefault();
    const files = e.dataTransfer.files;
	temp = files;
    uploadFile(files,1);
});
*/
function uploadFile(files, flag = 0) {
    var file = files ? files[0] : input_file.files[0];

    const baseImageUrl = document.getElementById('baseImageUrl').value;
    if (file) {
        
            img_view.innerHTML = `
                <img src="${baseImageUrl}file_image.svg" style="width: 100px; height: 100px; margin: 20px;" alt="">
                <p>${file.name}</p>
				<button type="submit" name="submit-click" value="compress" class="compress-btn pt-serif">Compress file</button>
                <button type="submit" name="submit-click" value="decompress" class="compress-btn pt-serif">Decompress file</button>
			`;
        
    } else {
        img_view.innerHTML = `
            <img src="${baseImageUrl}file_image.svg" style="width: 100px; height: 100px; margin: 20px;" alt="">
            <p>Click here <br>to upload text</p>
            <span>Upload any text files from desktop</span>`;
    }
}
/*
function postFile(file, operation) {
    if (!file) {
        alert("No file selected."); 
        return;
    }

    const formData = new FormData(); 
    formData.append("file", file); 
    formData.append("submit-click", operation);

    
    document.getElementById("loader").style.display = "block";

    fetch("http://localhost:8082/file-quash/compress-file", {
        method: "POST",
        body: formData,
    })
    .then(response => {
        if (!response.ok) {
            alert("Network response was not ok");
        } else {
            return response.text(); 
        }
    })
    .then(html => {

        document.getElementById("loader").style.display = "none";
        
        // Replace the page content with the new HTML
        document.open();
        document.write(html);
        document.close();
    })
    .catch(error => {
        alert("Error uploading file: " + error); 
        document.getElementById("loader").style.display = "none";
    });
} */



function routeLinkedIn() {
	window.location.href = "https://www.linkedin.com/in/shreeyash-janawade/";
}

function routeMail() {
	window.location.href = "mailto:shreeyashjanawade@gmail.com";
}


function pricingPopUp(){
    
Swal.fire({
  title: "You are Lucky!!",
  text: "This service is completely free for you",
  imageUrl: "/images/free.jpg",
  imageWidth: 400,
  imageHeight: 200,
  imageAlt: "Custom image"
});
}