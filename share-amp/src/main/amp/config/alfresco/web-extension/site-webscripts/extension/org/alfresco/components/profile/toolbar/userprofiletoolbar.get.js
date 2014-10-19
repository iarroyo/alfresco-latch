function addLink(id, href, msgId, msgArgs)
{
   model.links.push(
   {
      id: id,
      href: href,
      cssClass: (model.activePage == href) ? "theme-color-4" : null,
      label: msg.get(msgId, msgArgs ? msgArgs : null)
   });
}

//Add latch link
addLink("latch-link", "latch", "link.latch");