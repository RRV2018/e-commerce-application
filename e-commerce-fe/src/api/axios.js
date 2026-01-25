import axios from "axios";

const api = axios.create({
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
if (!config.url.startsWith("/api/auth")) {
  const token = sessionStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  }
  return config;

});

export default api;
