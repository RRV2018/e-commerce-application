# =========================
# 1️⃣ Build Stage
# =========================
FROM node:18-alpine AS build

# Install common tools (optional but useful)
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy dependency files first (better caching)
COPY package*.json ./

# Install dependencies
RUN npm install
RUN npm install react-icons


# Copy source code
COPY . .

# Build React app
RUN npm run build


# =========================
# 2️⃣ Runtime Stage (Nginx)
# =========================
FROM nginx:alpine

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy build output from previous stage
# For Vite → /app/dist
# For CRA → /app/build
COPY --from=build /app/build /usr/share/nginx/html

# Expose frontend port
EXPOSE 3001

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
