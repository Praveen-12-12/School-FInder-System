const schools = [
    
    { 
        name: "Hilton Matriculation Higher Secondary School", 
        type: "StateBoard", 
        rating: 4.5, 
        location: "11, 4th Cross Rd, East, Chromepet, Chennai, Tamil Nadu 600044 ",
        activity: "Sports, Cultural",
        link: "https://www.statecommonboard.org/" 
      },
      { 
        name: "The Lords’ International School	", 
        type: "ICSE", 
        rating: 3.9, 
        location: "#8, Vadivel Street, West Tambaram,Chennai – 600 045",
        activity: "Sports, Cultural",
        link: "http://www.lordsinternationalschool.com/" 
      },
    { 
      name: "A. K. T. Memorial Vidya Saaket School", 
      type: "CBSE", 
      rating: 4.2, 
      location: " Neelamangalam village kallakurichi - post, villupuram Distt., Tamilnadu,- 606202",
      activity: "Sports, Cultural",
      link: "https://www.aktcbse.com"
    },
    { 
        name: "The Blue Mountains School", 
        type: "ICSE", 
        rating: 3.9, 
        location: "Dilkhush Mahal,Ootacamund 643 001,The Nilgiris, Tamil Nadu	",
        activity: "Sports, Cultural",
        link: "http://www.bluemountainsschool.com/" 
      },
    { 
      name: "Abs Vidhya Mandhir", 
      type: "CBSE", 
      rating: 4.8, 
      location: "Thalakancherry road, iveli agaram, thiruvallur,",
      activity: "Sports, Cultural",
      link: "https://www.absvidhyamandhir.org" 
    },
    
    { 
        name: "Annai Violet Matriculation & Higher Secondary School", 
        type: "StateBoard", 
        rating: 4.5, 
        location: "1, 5th Main Rd, Anna Nagar, Chennai, Tamil Nadu 600040  ",
        activity: "Sports, Cultural",
        link: "https://www.statecommonboard.org/" 
      },
      { 
        name: "Excel Central School	", 
        type: "ICSE", 
        rating: 3.0, 
        location: "17-190A, Awai Farm Lane,Thiruvattar – 629177,Tamil Nadu.	",
        activity: "Sports, Cultural",
        link: "http://excelschools.edu.in/excelcentral/index.html" 
      },
    
      { 
        name: " Sri Sankara Matriculation School", 
        type: "StateBoard", 
        rating: 4.0, 
        location: "40, Vivekananda St, Thoraipakkam, Chennai, Tamil Nadu 600041  ",
        activity: "Sports, Cultural",
        link: "https://www.statecommonboard.org/" 
      },
        { 
      name: "Acharya Mahashraman Terapanth Jain Public School", 
      type: "CBSE", 
      rating: 3.9, 
      location: "57 & 58/1, THATTANKULAM ROAD, MADHAVARAM, CHENNAI, TAMILNADU,",
      activity: "Sports, Cultural",
      link: "https://www.amtjainpublicschool.com" 
    },
    { 
        name: " Sishya school	", 
        type: "ICSE", 
        rating: 4.9, 
        location: "15, Padmanabha Nagar,Adyar, Chennai-600020		",
        activity: "Sports, Cultural",
        link: "http://www.sishya.com/" 
      },
      { 
        name: "  Sri Mylai Karpagavalli Matriculation Higher Secondary School", 
        type: "StateBoard", 
        rating: 4.0, 
        location: "45, 1st Cross St, Adyar, Chennai, Tamil Nadu 600020   ",
        activity: "Sports, Cultural",
        link: "https://www.statecommonboard.org/" 
      },
    { 
      name: "Agarwal Vidyalaya And Junior College", 
      type: "CBSE", 
      rating: 4.5, 
      location: "NO.54, EVK SAMPATH ROAD, VEPERY, CHENNAI,",
      activity: "Sports, Cultural",
      link: "http://www.agarwalvidyalaya.com/" 
    },
    
    { 
        name: "  Maharishi Vidya Mandir Senior Secondary School", 
        type: "StateBoard", 
        rating: 4.0, 
        location: "19, 5th Main Rd, Anna Nagar, Chennai, Tamil Nadu 600040   ",
        activity: "Sports, Cultural",
        link: "https://www.statecommonboard.org/" 
      },
      { 
        name: " Abacus Montessori School", 
        type: "ICSE", 
        rating: 4.1, 
        location: "3 Thirumalai Nagar Annexe III Main Road,Perungudi, Chennai 600 096",
        activity: "Sports, Cultural",
        link: "http://www.abacusnow.com/" 
      },
      { 
        name: "  Vana Vani Matriculation Higher Secondary School", 
        type: "StateBoard", 
        rating: 3.5, 
        location: "4, 10th Street, Kodambakkam, Chennai, Tamil Nadu 600031  ",
        activity: "Sports, Cultural",
        link: "https://www.statecommonboard.org/" 
      },
  ];
  
  function searchSchools() {
    const searchInput = document.getElementById("searchInput").value.toLowerCase();
    const filterType = document.getElementById("typeFilter").value;
    const locationInput = document.getElementById("locationInput").value.toLowerCase();
    
  
  
    const results = schools.filter(school => {
      const matchName = school.name.toLowerCase().includes(searchInput);
      const matchType = filterType === "all" || school.type === filterType;
      const matchLocation = school.location.toLowerCase().includes(locationInput);
      
      return matchName && matchType && matchLocation;
    });
  
    displaySchools(results);
  }
  
  function displaySchools(schoolArray) {
    const listContainer = document.getElementById("schoolList");
    listContainer.innerHTML = "";
  
    if (schoolArray.length === 0) {
      listContainer.innerHTML = "<p>No schools found.</p>";
      return;
    }
  
    schoolArray.forEach(school => {
      const card = document.createElement("div");
      card.className = "school-card";
      card.innerHTML = `
        <h3>${school.name}</h3>
        <p><strong>Type:</strong> ${school.type}</p>
        <p><strong>Rating:</strong> ⭐ ${school.rating}</p>
        <p><strong>Location:</strong> 📍 ${school.location}</p>
        <p><strong>Activity:</strong> 💪 ${school.activity}</p>
        <a href="${school.link}" target="_blank" class="visit-button">Visit Website</a>
      `;
      listContainer.appendChild(card);
    });
  }
  
  displaySchools(schools);

  document.getElementById("resetBtn").addEventListener("click", () => {
    // Clear all input fields
    document.getElementById("searchInput").value = "";
    document.getElementById("typeFilter").value = "all";
    
    const locationInput = document.getElementById("locationInput");
    if (locationInput) locationInput.value = "";
  
    // Re-display all schools
    displaySchools(schools);
  });
  
    
  