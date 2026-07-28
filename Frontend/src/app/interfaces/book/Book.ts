import { BookCopy } from "../book-copy/book-copy";

export interface Book {
  id: number;
  title: string;
  author: string[];
  isbn: string;
  img: string;
  available : boolean;
  
genres?: string[]; 
  
copies?: BookCopy[]; 
}