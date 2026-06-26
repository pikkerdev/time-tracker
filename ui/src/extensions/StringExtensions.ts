String.prototype.toTitleCase = function() {
  return this.replace(/\b\w+/g, w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
}

export {}
