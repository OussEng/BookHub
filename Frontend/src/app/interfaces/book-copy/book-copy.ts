import { BookCondition } from "./book-copy-condition";

export interface BookCopy {
  id: number;
  condition: BookCondition;
  bookId: number; 
  isAvailable: boolean; 
}