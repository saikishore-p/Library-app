
class BookModel {

    // The variables and the attributes of what the backend spring boot application is looing for, for a Book 
    // and what the database is looking for a book



    id: number;
    title: string;
    author?: string; // ? means optional variable which means this can be null
    description?: string;
    copies?: number;
    copiesAvailable?: number;
    category?: string;
    img?: string;

    constructor(id: number, title: string, author: string, description: string,
        copies: number, copiesAvailable: number, category: string, img: string) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.copies = copies;
        this.copiesAvailable = copiesAvailable;
        this.category = category;
        this.img = img;
    }
}

export default BookModel