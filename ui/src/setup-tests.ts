import 'src/extensions/ArrayExtensions'
import 'src/extensions/StringExtensions'

// not provided by jsdom
Element.prototype.animate = (() => ({cancel: () => {}})) as any
