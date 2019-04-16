Group Project - README Template
===

# Quest

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Quest is a Product-Review app, where customers who wish to buy / who have bought a product can read / write reviews about the product. It also shows the latest, popular and trending products.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category: People and Blogs **
- **Mobile: This app would be primarily developed for mobile**
- **Story: Allows users to comment and see trending products and write reviews on their favorite products.**
- **Market: Any individual could choose to use this app, and to keep it a safe environment.**
- **Habit: This app could be used as often or unoften as the user wanted depending on how deep their social life is, and what exactly they're looking for.**
- **Scope: We would like for anyone who wants to rate products to be able to use and play with the app. **

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can sign up or login in.
* User can browse without logging-in / signing-up.
* User can logout from the application.
* User Timeline
* Prodcut Search
 

**Optional Nice-to-have Stories**

* Product Adding to shelf
* Product Viewing shelf
* Product (Home -> ProductDetail Browse flow)
* Product Home Popular & BestReview Products
* User can see top level categories and popular products.
* Set up backend using parse to store product, review and user information.
* For each product selected, user can see the following details:
* Product Name, Brand, Rating and Reviews
* User can write a review to the product.
* Night Mode


### 2. Screen Archetypes

* Login
* Search Product View
* Individual Item View
* Create Comment/Rate Section
    * Like Comment
* Email A Owner
    * Emails an owner with concerns
* Report Prices
    * Reports the prices of products
* Profile Screen
    * Lets people manage their profile


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Product Selection
* Profile
* Settings

**Flow Navigation** (Screen to Screen)

* Forced Log in
* Product View
* Profile -> Text field to be modified

## Wireframes

<img src="https://i.imgur.com/AYsDIOb.jpg"
width=600>

## Schema 
### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts where user is author
         ```swift
         let query = PFQuery(className:"Post")
         query.whereKey("author", equalTo: currentUser)
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // TODO: Do something with posts...
            }
         }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
