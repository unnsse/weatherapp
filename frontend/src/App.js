import React, { useState } from 'react';
import './App.css';

function App() {
  // State to hold each part of the address
  const [street, setStreet] = useState("");
  const [city, setCity] = useState("");
  const [state, setState] = useState("");
  const [zipCode, setZipCode] = useState("");

  const [weather, setWeather] = useState(null);
  const [isFromCache, setIsFromCache] = useState(false);

  const handleSubmit = (event) => {
    event.preventDefault();
    const address = { street, city, state, zipCode };
    fetchWeatherData(address);
  };

  const fetchWeatherData = async (address) => {
    try {
      const response = await fetch('http://localhost:8080/api/weather', {
        method: 'POST', // Use POST method
        headers: {
          'Content-Type': 'application/json', // Set Content-Type to application/json
        },
        body: JSON.stringify(address), // Send the entire address object as the body
      });

      if (response.ok) {
        const data = await response.json();
        setWeather(data);
        setIsFromCache(data.fromCache || false); // Assuming your API returns 'fromCache'
      } else {
        console.error("Error fetching weather data:", response.statusText);
      }
    } catch (error) {
      console.error("Error fetching weather data:", error);
    }
  };

  return (
    <div className="App">
      <h1>Weather Forecast</h1>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={street}
          onChange={(e) => setStreet(e.target.value)}
          placeholder="Street"
        />
        <input
          type="text"
          value={city}
          onChange={(e) => setCity(e.target.value)}
          placeholder="City"
        />
        <input
          type="text"
          value={state}
          onChange={(e) => setState(e.target.value)}
          placeholder="State"
        />
        <input
          type="text"
          value={zipCode}
          onChange={(e) => setZipCode(e.target.value)}
          placeholder="Zip Code"
        />
        <button type="submit">Get Weather</button>
      </form>

      {weather && (
        <div>
          <h2>Weather Details</h2>
          <p>Temperature: {weather.temperature}</p>
          <p>Short Forecast: {weather.shortForecast}</p>
          <p>Detailed Forecast: {weather.detailedForecast}</p>
          {isFromCache && <p><strong>Data fetched from cache</strong></p>}
        </div>
      )}
    </div>
  );
}

export default App;