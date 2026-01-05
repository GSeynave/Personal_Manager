export interface TransactionCategoryDTO {
  id: number;
  title: string;
  icon: string | null;
  description: string | null;
  expectedAmount: number | null;
  parentCategoryId: number | null;
  parentCategoryTitle: string | null;
  subCategories: TransactionCategoryDTO[] | null;
}

export interface CreateTransactionCategoryRequest {
  title: string;
  icon?: string;
  description?: string;
  expectedAmount?: number;
  parentCategoryId?: number;
}

export interface UpdateTransactionCategoryRequest {
  title?: string;
  icon?: string;
  description?: string;
  expectedAmount?: number;
  parentCategoryId?: number;
}

export default class TransactionCategory {
  id: number;
  title: string;
  icon: string | null;
  description: string | null;
  expectedAmount: number | null;
  parentCategoryId: number | null;
  parentCategoryTitle: string | null;
  subCategories: TransactionCategory[] | null;

  constructor(
    id: number,
    title: string,
    icon: string | null = null,
    description: string | null = null,
    expectedAmount: number | null = null,
    parentCategoryId: number | null = null,
    parentCategoryTitle: string | null = null,
    subCategories: TransactionCategory[] | null = null
  ) {
    this.id = id;
    this.title = title;
    this.icon = icon;
    this.description = description;
    this.expectedAmount = expectedAmount;
    this.parentCategoryId = parentCategoryId;
    this.parentCategoryTitle = parentCategoryTitle;
    this.subCategories = subCategories;
  }

  static fromDTO(dto: TransactionCategoryDTO): TransactionCategory {
    return new TransactionCategory(
      dto.id,
      dto.title,
      dto.icon,
      dto.description,
      dto.expectedAmount,
      dto.parentCategoryId,
      dto.parentCategoryTitle,
      dto.subCategories ? dto.subCategories.map(sub => TransactionCategory.fromDTO(sub)) : null
    );
  }
}
