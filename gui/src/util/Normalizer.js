export function normalizeTransaction(t) {
    if (!t)
        return null

    return {
        id: t.id,
        date: t.date,
        amountInCents: t.amount,
        isPeriodic: t.isPeriodic,
        categoryId: t.category,
    }
}

export function denormalizeTransaction(t) {
    if (!t)
        return null

    return {
        id: t.id,
        date: t.date,
        amount: t.amountInCents,
        isPeriodic: t.isPeriodic,
        category: t.categoryId,
    }
}

export function normalizeStatement(stmt) {
    if (!stmt)
        return null

    const transactions = (stmt.transactions || []).map(t => normalizeTransaction(t))

    return {
        id: stmt.id,
        date: stmt.date,
        finalBalanceInCents: parseInt(stmt.balance),
        transactions: transactions,
        previousStatementId: (stmt.previousStatement && stmt.previousStatement.id) || null,
        previousBalanceInCents: (stmt.previousStatement && stmt.previousStatement.balance) || null, //hier das klappt nbcihtw enns 0 ist
    }
}

export function denormalizeStatement(stmt) {
    if (!stmt)
        return null

    return {
        id: stmt.id,
        date: stmt.date,
        balance: stmt.finalBalanceInCents,
        transactions: stmt.transactions && stmt.transactions.map(t => denormalizeTransaction(t)),
        previousStatement: denormalizeStatement(stmt.previousStatement),
        volume: stmt.volumeInCents,
    }
}

export function denormalizeReason(rsn) {
    if (!rsn)
        return null

    return {
        ...rsn
    }
}

export function normalizeCategory(cat) {
    if (!cat)
        return null

    return {
        id: cat.id || null,
        parentId: cat.parentId || null,
        name: cat.name,
        description: cat.description,
    }
}

export function denormalizeCategory(cat) {
    return {
        id: cat.id,
        parentId: cat.parentId,
        name: cat.name,
        description: cat.description,

        subCategories: (cat.subCategories || []).map(denormalizeCategory),

        createdAt: cat.createdAt,
        updatedAt: cat.updatedAt,
    }
}

export function denormalizeTurnoverImport(turnoverImport) {
    return {
        id: turnoverImport.id,
        importedAt: turnoverImport.importedAt,

        turnovers: (turnoverImport.turnovers || []).map(denormalizeTurnoverRow),
    };
}

export function denormalizeTurnoverPreview(turnover) {
    return {
        filename: turnover.filename,
        uploadTime: turnover.uploadTime,
        rows: (turnover.rows || []).map(denormalizeTurnoverPreviewRow)
    };
}

export function denormalizeTurnoverRow(row) {
    return denormalizeTurnoverPreview(row);
}

export function denormalizeTurnoverPreviewRow(row) {
    return {
        date: row.date,
        amountInCents: row.amountInCents,
        categoryId: row.categoryId,
        suggestedCategory: row.suggestedCategory,
        description: row.description,
        recipient: row.recipient,
        checksum: row.checksum,
        importable: row.importable,
    };
}

export function normalizeTurnoverPreview(turnover) {
    return turnover;
}
