# Use the official NGINX base image
FROM nginx:latest

# Copy custom HTML files to the NGINX default directory
COPY ./html /usr/share/nginx/html

# Expose port 80 for the NGINX server
EXPOSE 80

# Start the NGINX server
CMD ["nginx", "-g", "daemon off;"]
