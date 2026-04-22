const IS_LOCAL = window.location.hostname === "127.0.0.1" || window.location.hostname === "localhost";
const API_BASE_URL = IS_LOCAL ? "http://localhost:8080/api/v1" : "https://api.menu-analyzer.loveesh.me/api/v1";

const menuImage = document.getElementById('menuImage');
const menuCamera = document.getElementById('menuCamera');
const selectedFileInfo = document.getElementById('selectedFileInfo');

menuImage.addEventListener('change', () => {
    if (menuImage.files.length > 0) {
        menuCamera.value = ""; // Clear camera input
        selectedFileInfo.innerHTML = "✅ Selected: <strong>" + menuImage.files[0].name + "</strong>";
        selectedFileInfo.classList.remove('d-none');
    } else {
        selectedFileInfo.classList.add('d-none');
    }
});

menuCamera.addEventListener('change', () => {
    if (menuCamera.files.length > 0) {
        menuImage.value = ""; // Clear gallery input
        selectedFileInfo.innerHTML = "✅ Selected: <strong>Camera Photo</strong>";
        selectedFileInfo.classList.remove('d-none');
    } else {
        selectedFileInfo.classList.add('d-none');
    }
});

document.getElementById('scanMenuForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    let selectedFile = null;
    if (menuImage.files.length > 0) selectedFile = menuImage.files[0];
    else if (menuCamera.files.length > 0) selectedFile = menuCamera.files[0];

    if (!selectedFile) {
        showError("Please upload an image or take a photo first.");
        return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);

    toggleLoading('scanLoading', true);
    document.getElementById('resultsContainer').innerHTML = ''; 

    try {
        const response = await fetch(`${API_BASE_URL}/menus/scan`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) throw new Error("Failed to scan menu. Make sure the backend is running.");

        const data = await response.json();
        renderMenuResponse(data);
    } catch (err) {
        showError(err.message);
    } finally {
        toggleLoading('scanLoading', false);
    }
});

document.getElementById('searchFoodForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const query = document.getElementById('searchQuery').value;
    if (!query.trim()) return;

    toggleLoading('searchLoading', true);
    document.getElementById('resultsContainer').innerHTML = ''; 

    try {
        const response = await fetch(`${API_BASE_URL}/foods/search?query=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error("Failed to search food. Make sure backend is running.");

        const data = await response.json();
        renderFoodSearchResponse(data);
    } catch (err) {
        showError(err.message);
    } finally {
        toggleLoading('searchLoading', false);
    }
});

function toggleLoading(elementId, show) {
    const el = document.getElementById(elementId);
    if (show) {
        el.classList.remove('d-none');
    } else {
        el.classList.add('d-none');
    }
}

function showError(msg) {
    document.getElementById('resultsContainer').innerHTML = `
        <div class="alert alert-danger" role="alert">
            <strong>Error:</strong> ${msg}
        </div>
    `;
}

function renderMenuResponse(menuData) {
    if (!menuData || !menuData.dishes || menuData.dishes.length === 0) {
        showError("No dishes found in the image. Please try another menu.");
        return;
    }

    let html = `
        <div class="card glass-card result-card mb-4 p-3 p-md-4">
            <h2 class="mb-4 d-flex align-items-center gap-2">
                <span style="font-size: 2rem;">🍽️</span> 
                <span class="text-primary fw-bold" style="background: linear-gradient(135deg, #0d6efd, #0b5ed7); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">Scanned Menu Results</span>
            </h2>
            <div class="row">
    `;

    menuData.dishes.forEach((dish, idx) => {
        html += `
            <div class="col-md-6 mb-4" id="dish-col-${dish.id}">
                ${renderSingleDish(dish, menuData.id)}
            </div>
        `;
    });

    html += `</div></div>`;
    document.getElementById('resultsContainer').innerHTML = html;
}

function renderSingleDish(dish, menuId) {
    let nutritionHtml = '';
    let n = dish.nutrition;
    
    if (n) {
        nutritionHtml = `
            <div class="d-flex flex-wrap mt-3 pt-3 border-top border-light">
                 <div class="nutrition-badge">Cal: <span class="nutrition-value text-danger">${n.calories || 'N/A'}</span></div>
                 <div class="nutrition-badge">Pro: <span class="nutrition-value text-primary">${n.proteinGrams || 0}g</span></div>
                 <div class="nutrition-badge">Carb: <span class="nutrition-value text-success">${n.carbsGrams || 0}g</span></div>
                 <div class="nutrition-badge">Fat: <span class="nutrition-value text-warning">${n.fatGrams || 0}g</span></div>
            </div>
        `;
    } else {
        nutritionHtml = `
            <div id="nutrition-container-${dish.id}" class="mt-3 pt-2 border-top border-light">
                <p class="text-warning small mb-2"><em>Nutrition data not available.</em></p>
                <button class="btn btn-sm btn-outline-primary px-3 rounded-pill" onclick="enrichDish(${menuId}, ${dish.id})">Get Nutrition Data</button>
            </div>
        `;
    }

    let imageHtml = '';
    if (dish.imageUrl && dish.imageUrl !== 'null') {
         imageHtml = `<img src="${dish.imageUrl}" class="w-100 rounded mb-3 shadow-sm" style="height: 180px; object-fit: cover;" alt="${dish.name}">`;
    } else {
         imageHtml = `<div class="w-100 rounded mb-3 shadow-sm d-flex flex-column align-items-center justify-content-center bg-light text-secondary" style="height: 180px;"><span style="font-size: 2rem;">🍽️</span><span class="small mt-2">No Image</span></div>`;
    }

    let dietBadge = '';
    if (dish.dietType && dish.dietType !== 'UNKNOWN' && dish.dietType !== null) {
         dietBadge = `<span class="badge bg-success px-3 py-2 rounded-pill mb-3 shadow-sm" style="font-size: 0.85rem;">🌱 ${dish.dietType}</span>`;
    }
    
    let recipeInfo = '';
    if (dish.recipeText && dish.recipeText !== "Recipe details not provided by external source.") {
         recipeInfo = `<div class="mt-3 p-3 bg-white bg-opacity-50 rounded border-start border-4 border-primary"><p class="small text-muted mb-0 fst-italic" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;"><span class="fw-bold text-dark">Recipe snippet:</span> ${dish.recipeText}</p></div>`;
    }

    return `
        <div class="dish-inner-card p-3 p-md-4 h-100 position-relative overflow-hidden">
            ${imageHtml}
            ${dietBadge}
            <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="fw-bold mb-0 text-dark fs-4">${dish.name || 'Unknown Dish'}</h5>
                ${dish.price ? `<span class="badge bg-primary fs-5 rounded-pill shadow-sm py-2 px-3">₹${dish.price}</span>` : ''}
            </div>
            <p class="text-muted small mb-0">${dish.description || ''}</p>
            ${recipeInfo}
            ${nutritionHtml}
        </div>
    `;
}

function renderFoodSearchResponse(data) {
    if (!data) return;

    let n = data.nutrition || {};

    let imageHtml = '';
    if (data.imageUrl && data.imageUrl !== 'null') {
         imageHtml = `<img src="${data.imageUrl}" class="rounded-circle shadow border border-4 border-white mb-4" style="width: 150px; height: 150px; object-fit: cover;" alt="${data.name}">`;
    } else {
         imageHtml = `<div class="rounded-circle shadow border border-4 border-white mb-4 d-flex align-items-center justify-content-center bg-light text-secondary mx-auto" style="width: 150px; height: 150px;"><span style="font-size: 3rem;">🍽️</span></div>`;
    }

    let dietBadge = '';
    if (data.dietType && data.dietType !== 'UNKNOWN' && data.dietType !== null) {
         dietBadge = `<span class="badge bg-success px-3 py-2 rounded-pill mt-3 mb-2 shadow-sm fs-6">🌱 ${data.dietType}</span>`;
    }
    
    let recipeInfo = '';
    if (data.recipeText && data.recipeText !== "Recipe details not provided by external source.") {
         recipeInfo = `<p class="text-muted fst-italic mt-3 mx-auto" style="max-width: 600px;">" ${data.recipeText} "</p>`;
    }

    let html = `
        <div class="card glass-card result-card p-3 p-md-5">
            <h2 class="mb-4 text-secondary fw-bold">🔍 Search Results</h2>
            <div class="dish-inner-card p-3 p-md-4 text-center">
                ${imageHtml}
                <h3 class="fw-bold text-capitalize text-dark mb-2 display-6">${data.name || document.getElementById('searchQuery').value}</h3>
                ${dietBadge}
                ${recipeInfo}
                <div class="d-flex flex-wrap justify-content-center gap-3 mt-4">
                     <div class="nutrition-badge fs-5 py-3 px-4 shadow-sm bg-white">🔥 Calories: <span class="nutrition-value text-danger fs-4">${n.calories || 0}</span></div>
                     <div class="nutrition-badge fs-5 py-3 px-4 shadow-sm bg-white">🥩 Protein: <span class="nutrition-value text-primary fs-4">${n.proteinGrams || 0}g</span></div>
                     <div class="nutrition-badge fs-5 py-3 px-4 shadow-sm bg-white">🍞 Carbs: <span class="nutrition-value text-success fs-4">${n.carbsGrams || 0}g</span></div>
                     <div class="nutrition-badge fs-5 py-3 px-4 shadow-sm bg-white">🧈 Fat: <span class="nutrition-value text-warning fs-4">${n.fatGrams || 0}g</span></div>
                </div>
            </div>
        </div>
    `;

    document.getElementById('resultsContainer').innerHTML = html;
}

async function enrichDish(menuId, dishId) {
    const dishCol = document.getElementById(`dish-col-${dishId}`);
    if (!dishCol) return;
    
    const nutritionContainer = document.getElementById(`nutrition-container-${dishId}`);
    if (nutritionContainer) {
        nutritionContainer.innerHTML = `<div class="d-flex align-items-center gap-2 mt-2"><div class="spinner-border spinner-border-sm text-primary" role="status"></div><span class="small text-muted">Analyzing ingredients...</span></div>`;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/menus/${menuId}/dishes/${dishId}/enrich`, {
            method: 'POST'
        });

        if (!response.ok) throw new Error("Failed to enrich dish.");

        const dish = await response.json();
        
        // RE-RENDER the entire single dish column!
        dishCol.innerHTML = renderSingleDish(dish, menuId);

    } catch (err) {
        if (nutritionContainer) {
            nutritionContainer.innerHTML = `
                <p class="text-danger small mt-2 mb-1">Error fetching data.</p>
                <button class="btn btn-sm btn-outline-danger px-3 rounded-pill" onclick="enrichDish(${menuId}, ${dishId})">Retry</button>
            `;
        }
    }
}
