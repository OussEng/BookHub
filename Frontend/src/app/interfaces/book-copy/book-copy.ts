import { BookCondition } from "./book-copy-condition";
import { BookStatus } from "./book-copy-status";

export interface BookCopy {
  id: number;
  serialNumber : string;
  condition: BookCondition;
  bookId: number; 
  isAvailable: boolean; 
  bookStatus: BookStatus;
}