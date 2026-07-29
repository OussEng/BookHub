import { BookCopy } from "../book-copy/book-copy";

export interface Book {
  id: number;
  title: string;
  img: string;
  description: string;
  isbn: string;
  genres: string[];
  publishDate: string;
  author: string[];
  available: boolean;
}